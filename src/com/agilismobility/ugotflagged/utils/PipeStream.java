package com.agilismobility.ugotflagged.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PipeStream extends ByteArrayOutputStream {
	private InputStream is;
	private String data;

	public PipeStream(InputStream is) {
		super(1024);
		this.is = is;
	}

	private InputStream getInputStream() {
		return new ByteArrayInputStream(buf, 0, count);
	}

	public final InputStream peek() throws Exception {
		read();
		data = new String(buf, 0, count);
		return getInputStream();
	}

	private void read() {
		byte[] data = new byte[10240];
		int size;
		try {
			while ((size = is.read(data)) != -1) {
				write(data, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getData() {
		return data;
	}
}
