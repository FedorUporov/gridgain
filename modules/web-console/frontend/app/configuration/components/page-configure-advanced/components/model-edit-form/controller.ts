/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * Commons Clause Restriction
 * 
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 * 
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 * 
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

import cloneDeep from 'lodash/cloneDeep';
import _ from 'lodash';
import get from 'lodash/get';

import {default as Models} from '../../../../services/Models';
import {default as ModalImportModels} from '../../../../components/modal-import-models/service';
import {default as IgniteVersion} from 'app/services/Version.service';
import {Confirm} from 'app/services/Confirm.service';
import {DomainModel} from '../../../../types';
import ErrorPopover from 'app/services/ErrorPopover.service';
import LegacyUtilsFactory from 'app/services/LegacyUtils.service';
import ConfigChangesGuard from '../../../../services/ConfigChangesGuard';
import FormUtils from 'app/services/FormUtils.service';

export default class ModelEditFormController {
    model: DomainModel;
    onSave: ng.ICompiledExpression;

    static $inject = ['ModalImportModels', 'IgniteErrorPopover', 'IgniteLegacyUtils', 'Confirm', 'ConfigChangesGuard', 'IgniteVersion', '$scope', 'Models', 'IgniteFormUtils'];

    constructor(
        private ModalImportModels: ModalImportModels,
        private ErrorPopover: ErrorPopover,
        private LegacyUtils: ReturnType<typeof LegacyUtilsFactory>,
        private Confirm: Confirm,
        private ConfigChangesGuard: ConfigChangesGuard,
        private IgniteVersion: IgniteVersion,
        private $scope: ng.IScope,
        private Models: Models,
        private IgniteFormUtils: ReturnType<typeof FormUtils>
    ) {}

    javaBuiltInClassesBase = this.LegacyUtils.javaBuiltInClasses;

    $onInit() {
        this.available = this.IgniteVersion.available.bind(this.IgniteVersion);

        this.queryFieldTypes = this.LegacyUtils.javaBuiltInClasses.concat('byte[]');
        this.$scope.ui = this.IgniteFormUtils.formUI();

        this.$scope.javaBuiltInClasses = this.LegacyUtils.javaBuiltInClasses;
        this.$scope.supportedJdbcTypes = this.LegacyUtils.mkOptions(this.LegacyUtils.SUPPORTED_JDBC_TYPES);
        this.$scope.supportedJavaTypes = this.LegacyUtils.mkOptions(this.LegacyUtils.javaBuiltInTypes);

        this.formActions = [
            {text: 'Save', icon: 'checkmark', click: () => this.save()},
            {text: 'Save and Download', icon: 'download', click: () => this.save(true)}
        ];
    }

    /**
     * Create list of fields to show in index fields dropdown.
     * @param cur Current queryKeyFields
     */
    fields(prefix: string, cur: string[]) {
        const fields = this.$scope.backupItem
            ? _.map(this.$scope.backupItem.fields, (field) => ({value: field.name, label: field.name}))
            : [];

        if (prefix === 'new')
            return fields;

        _.forEach(_.isArray(cur) ? cur : [cur], (value) => {
            if (!_.find(fields, {value}))
                fields.push({value, label: value + ' (Unknown field)'});
        });

        return fields;
    }

    importModels() {
        return this.ModalImportModels.open();
    }

    checkQueryConfiguration(item: DomainModel) {
        if (item.queryMetadata === 'Configuration' && this.LegacyUtils.domainForQueryConfigured(item)) {
            if (_.isEmpty(item.fields))
                return this.ErrorPopover.show('queryFields', 'Query fields should not be empty', this.$scope.ui, 'query');

            const indexes = item.indexes;

            if (indexes && indexes.length > 0) {
                if (_.find(indexes, (index, idx) => {
                    if (_.isEmpty(index.fields))
                        return !this.ErrorPopover.show('indexes' + idx, 'Index fields are not specified', this.$scope.ui, 'query');

                    if (_.find(index.fields, (field) => !_.find(item.fields, (configuredField) => configuredField.name === field.name)))
                        return !this.ErrorPopover.show('indexes' + idx, 'Index contains not configured fields', this.$scope.ui, 'query');
                }))
                    return false;
            }
        }

        return true;
    }

    checkStoreConfiguration(item: DomainModel) {
        if (this.LegacyUtils.domainForStoreConfigured(item)) {
            if (this.LegacyUtils.isEmptyString(item.databaseSchema))
                return this.ErrorPopover.show('databaseSchemaInput', 'Database schema should not be empty', this.$scope.ui, 'store');

            if (this.LegacyUtils.isEmptyString(item.databaseTable))
                return this.ErrorPopover.show('databaseTableInput', 'Database table should not be empty', this.$scope.ui, 'store');

            if (_.isEmpty(item.keyFields))
                return this.ErrorPopover.show('keyFields', 'Key fields are not specified', this.$scope.ui, 'store');

            if (this.LegacyUtils.isJavaBuiltInClass(item.keyType) && item.keyFields.length !== 1)
                return this.ErrorPopover.show('keyFields', 'Only one field should be specified in case when key type is a Java built-in type', this.$scope.ui, 'store');

            if (_.isEmpty(item.valueFields))
                return this.ErrorPopover.show('valueFields', 'Value fields are not specified', this.$scope.ui, 'store');
        }

        return true;
    }

    /**
     * Check domain model logical consistency.
     */
    validate(item: DomainModel) {
        if (!this.checkQueryConfiguration(item))
            return false;

        if (!this.checkStoreConfiguration(item))
            return false;

        if (!this.LegacyUtils.domainForStoreConfigured(item) && !this.LegacyUtils.domainForQueryConfigured(item) && item.queryMetadata === 'Configuration')
            return this.ErrorPopover.show('query-title', 'SQL query domain model should be configured', this.$scope.ui, 'query');

        if (!this.LegacyUtils.domainForStoreConfigured(item) && item.generatePojo)
            return this.ErrorPopover.show('store-title', 'Domain model for cache store should be configured when generation of POJO classes is enabled', this.$scope.ui, 'store');

        return true;
    }

    $onChanges(changes) {
        if (
            'model' in changes && get(this.$scope.backupItem, '_id') !== get(this.model, '_id')
        ) {
            this.$scope.backupItem = cloneDeep(changes.model.currentValue);
            if (this.$scope.ui && this.$scope.ui.inputForm) {
                this.$scope.ui.inputForm.$setPristine();
                this.$scope.ui.inputForm.$setUntouched();
            }
        }
        if ('caches' in changes)
            this.cachesMenu = (changes.caches.currentValue || []).map((c) => ({label: c.name, value: c._id}));
    }

    onQueryFieldsChange(model: DomainModel) {
        this.$scope.backupItem = this.Models.removeInvalidFields(model);
    }

    getValuesToCompare() {
        return [this.model, this.$scope.backupItem].map(this.Models.normalize);
    }

    save(download) {
        if (this.$scope.ui.inputForm.$invalid)
            return this.IgniteFormUtils.triggerValidation(this.$scope.ui.inputForm, this.$scope);

        if (!this.validate(this.$scope.backupItem))
            return;

        this.onSave({$event: {model: cloneDeep(this.$scope.backupItem), download}});
    }

    reset = (forReal: boolean) => forReal ? this.$scope.backupItem = cloneDeep(this.model) : void 0;

    confirmAndReset() {
        return this.Confirm.confirm('Are you sure you want to undo all changes for current model?').then(() => true)
        .then(this.reset)
        .catch(() => {});
    }
}
