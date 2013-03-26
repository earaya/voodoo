package com.earaya.voodoo.filters.answers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.MultivaluedMap;

public final class ContainsHeaderAnswer implements Answer<Boolean> {

    private final MultivaluedMap<String, String> headers;

    private ContainsHeaderAnswer(final MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }

    public static ContainsHeaderAnswer create(final MultivaluedMap<String, String> headers) {
        return new ContainsHeaderAnswer(headers);
    }

    /**
     * @param invocation the invocation on the mock.
     * @return the value to be returned
     * @throws Throwable the throwable to be thrown
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public Boolean answer(InvocationOnMock invocation) throws Throwable {
        final Object[] arguments = invocation.getArguments();
        return arguments != null && arguments.length >= 1 && headers.containsKey(arguments[0]);
    }
}
