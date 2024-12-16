package de.deepamehta.core.service;

import java.io.IOException;
import java.io.InputStream;


public interface Plugin {
    public abstract InputStream getResourceAsStream(String name) throws IOException;
}