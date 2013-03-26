package com.earaya.voodoo.testing;

import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.List;

public final class VoodooResourceRunner extends BlockJUnit4ClassRunner {

    private Object testInstance;
    private boolean hasBefores;
    private boolean hasAfters;

    public VoodooResourceRunner(final Class<?> cls) throws org.junit.runners.model.InitializationError {
        super(cls);
    }

    /**
     * Returns a new fixture for running a test. Default implementation executes
     * the test class's no-argument constructor (validation should have ensured
     * one exists).
     */
    @Override
    protected Object createTest() throws Exception {
        // Always return a new instance if there aren't any BeforeOnce or AfterOnce methods
        // on the test class
        if (!hasBefores && !hasAfters) return super.createTest();

        if (this.testInstance != null)
            return this.testInstance;

        this.testInstance = super.createTest();
        return this.testInstance;
    }

    /**
     * Constructs a {@code Statement} to run all of the tests in the test class. Override to add pre-/post-processing.
     * Here is an outline of the implementation:
     * <ul>
     * <li>Call {@link #runChild(Object, org.junit.runner.notification.RunNotifier)} on each object returned by {@link #getChildren()} (subject to any imposed filter and sort).</li>
     * <li>ALWAYS run all non-overridden {@code @BeforeClass} methods on this class
     * and superclasses before the previous step; if any throws an
     * Exception, stop execution and pass the exception on.
     * <li>ALWAYS run all non-overridden {@code @AfterClass} methods on this class
     * and superclasses before any of the previous steps; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     * </ul>
     *
     * @return {@code Statement}
     */
    @Override
    protected Statement classBlock(RunNotifier notifier) {
        try {
            this.testInstance = this.createTest();
        }
        catch (Throwable e) {
            return new Fail(e);
        }

        Statement statement = super.classBlock(notifier);
        statement = withBeforeOnces(statement);
        statement = withAfterOnces(statement);
        return statement;
    }

    private Statement withBeforeOnces(Statement statement) {
        final List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(BeforeOnce.class);
        this.hasBefores = !befores.isEmpty();
        return befores.isEmpty() ? statement : new RunBefores(statement, befores, this.testInstance);
    }

    private Statement withAfterOnces(Statement statement) {
        final List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(AfterOnce.class);
        this.hasAfters = !afters.isEmpty();
        return afters.isEmpty() ? statement : new RunAfters(statement, afters, this.testInstance);
    }
}
