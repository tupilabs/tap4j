/*
 * The MIT License
 * 
 * Copyright (c) 2010 tap4j team (see AUTHORS)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tap4j.ext.junit;

import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.tap4j.ext.junit.TapListener.Type;

/**
 * JUnit 4 TAP runner that adds the TAP JUnit listener that outputs TAP for 
 * suites.
 *
 * @since 4.0
 */
public class TapRunnerSuite extends Suite {

    /**
     * @param klass
     * @param suiteClasses
     * @throws InitializationError
     */
    public TapRunnerSuite(Class<?> klass, Class<?>[] suiteClasses)
            throws InitializationError {
        super(klass, suiteClasses);
    }

    /**
     * @param klass
     * @param runners
     * @throws InitializationError
     */
    public TapRunnerSuite(Class<?> klass, List<Runner> runners)
            throws InitializationError {
        super(klass, runners);
    }

    /**
     * @param klass
     * @param builder
     * @throws InitializationError
     */
    public TapRunnerSuite(Class<?> klass, RunnerBuilder builder)
            throws InitializationError {
        super(klass, builder);
    }

    /**
     * @param builder
     * @param klass
     * @param suiteClasses
     * @throws InitializationError
     */
    public TapRunnerSuite(RunnerBuilder builder, Class<?> klass,
            Class<?>[] suiteClasses) throws InitializationError {
        super(builder, klass, suiteClasses);
    }

    /**
     * @param builder
     * @param classes
     * @throws InitializationError
     */
    public TapRunnerSuite(RunnerBuilder builder, Class<?>[] classes)
            throws InitializationError {
        super(builder, classes);
    }

    /**
     * Adds TAP listener to the notifier before calling the super run method.
     */
    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(new TapListener(Type.SUITE));
        super.run(notifier);
    }

}
