package com.example.lamda.demo;

import java.io.IOException;
import java.io.Serializable;

/**
 * Message terminate consumer thread
 */

public class Terminate  implements Serializable {
    /**
	 * serializationUID for serialization
	 */
	private static final long serialVersionUID = 1L;
	
    public byte [] encode()
    {
        try {
            return SerDes.serialize(this);
        } catch (IOException e) {
            System.out.println("Terminate:encode" + e);
            return new byte[0];
        }
    }

}
