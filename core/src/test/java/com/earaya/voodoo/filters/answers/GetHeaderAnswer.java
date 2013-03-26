package com.earaya.voodoo.filters.answers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.MultivaluedMap;

public final class GetHeaderAnswer implements Answer<String> {

    private final MultivaluedMap<String, String> headers;

    private GetHeaderAnswer(final MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }

    public static GetHeaderAnswer create(final MultivaluedMap<String, String> headers) {
        return new GetHeaderAnswer(headers);
    }

    /**
     * @param invocation the invocation on the mock.
     * @return the value to be returned
     * @throws Throwable the throwable to be thrown
     */
    @Override
    public String answer(InvocationOnMock invocation) throws Throwable {
        final Object[] arguments = invocation.getArguments();
        if (arguments == null || arguments.length < 1) return null;
        if (!(arguments[0] instanceof String)) return null;

        final String arg = String.class.cast(arguments[0]);

        return headers.getFirst(arg);
    }
}
