/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.commandline;

import java.io.Console;
import java.io.PrintWriter;
import java.io.Reader;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link GridConsole} like {@link Console} proxy.
 */
public class GridConsoleAdapter implements GridConsole {
    /** Delegate. */
    private final Console delegate;

    /** */
    public static @Nullable GridConsoleAdapter getInstance() {
        Console console = System.console();

        return console == null ? null : new GridConsoleAdapter(console);
    }

    /** Constructor. */
    private GridConsoleAdapter(Console delegate) {
        if (delegate == null)
            throw new NullPointerException("Console is not available.");

        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override public PrintWriter writer() {
        return delegate.writer();
    }

    /** {@inheritDoc} */
    @Override public Reader reader() {
        return delegate.reader();
    }

    /** {@inheritDoc} */
    @Override public Console format(String fmt, Object... args) {
        return delegate.format(fmt, args);
    }

    /** {@inheritDoc} */
    @Override public Console printf(String format, Object... args) {
        return delegate.printf(format, args);
    }

    /** {@inheritDoc} */
    @Override public String readLine(String fmt, Object... args) {
        return delegate.readLine(fmt, args);
    }

    /** {@inheritDoc} */
    @Override public String readLine() {
        return delegate.readLine();
    }

    /** {@inheritDoc} */
    @Override public char[] readPassword(String fmt, Object... args) {
        return delegate.readPassword(fmt, args);
    }

    /** {@inheritDoc} */
    @Override public char[] readPassword() {
        return delegate.readPassword();
    }

    /** {@inheritDoc} */
    @Override public void flush() {
        delegate.flush();
    }
}
