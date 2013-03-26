package com.earaya.voodoo.filters.answers;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.MultivaluedMap;

public final class SetHeaderAnswer implements Answer<Void> {

    private final MultivaluedMap<String, String> headers;

    private SetHeaderAnswer(final MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }

    public static SetHeaderAnswer create(final MultivaluedMap<String, String> headers) {
        return new SetHeaderAnswer(headers);
    }

    /**
     * @param invocation the invocation on the mock.
     * @return the value to be returned
     * @throws Throwable the throwable to be thrown
     */
    @Override
    public Void answer(InvocationOnMock invocation) throws Throwable {
        final Object[] arguments = invocation.getArguments();
        if (arguments == null || arguments.length != 2) return null;
        if (!String.class.isInstance(arguments[0]) || !String.class.isInstance(arguments[1])) return null;

        final String key = String.class.cast(arguments[0]);
        final String val = String.class.cast(arguments[1]);

        headers.add(key, val);

        return null;
    }
}
