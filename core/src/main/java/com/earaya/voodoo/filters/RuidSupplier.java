package com.earaya.voodoo.filters;

import com.google.common.base.Supplier;

import java.util.Random;

class RuidSupplier implements Supplier<String> {

    private final Random r = new Random();

    /**
     * Retrieves an instance of the appropriate type. The returned object may or
     * may not be a new instance, depending on the implementation.
     *
     * @return an instance of the appropriate type
     */
    @Override
    public String get() {
        return get(8);
    }

    public String get(int len) {
        return generateRuid(len);
    }

    private String generateRuid(int len) {
        StringBuffer uid = new StringBuffer();
        for (int i = 0; i < len; i++) {
            int rand = r.nextInt(10000);
            int mod36 = rand % 36;
            encodeAndAdd(uid, mod36);
        }
        return uid.toString();
    }

    private void encodeAndAdd(StringBuffer ret, long mod36Val) {
        if (mod36Val < 10) {
            ret.append((char) (((int) '0') + (int) mod36Val));
        } else {
            ret.append((char) (((int) 'a') + (int) (mod36Val - 10)));
        }
    }
}
