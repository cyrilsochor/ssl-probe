package cz.horadraka.sslprobe;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

@SuppressWarnings({
        "java:S106",
        "java:S2095",
})
public class SSLProbe {

    private static final Pattern HTTP_PATTERN = Pattern.compile("https?://([^/:]*)(:(\\d*))?(/.*)?");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: " + SSLProbe.class.getName() + " <host> <port>");
            System.exit(1);
        }
        System.out.println("Java version: " + System.getProperty("java.version") + " vendor: " + System.getProperty("java.vendor"));
        try {
            final String host;
            final Integer port;

            final Matcher matcher = HTTP_PATTERN.matcher(args[0]);
            if (matcher.matches()) {
                host = matcher.group(1);
                final String portString = matcher.group(3);
                if (portString != null) {
                    port = Integer.valueOf(portString);
                } else {
                    port = null;
                }
            } else {
                host = args[0];
                if (args.length > 1) {
                    port = Integer.parseInt(args[1]);
                } else {
                    port = null;
                }
            }

            probe(host, port != null ? port.intValue() : 443);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void probe(final String host, final int port) throws IOException {
        System.out.println(format("Probing host %s, port %d", host, port));

        final SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        final SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);

        final InputStream in = sslsocket.getInputStream();
        final OutputStream out = sslsocket.getOutputStream();

        // Write a test byte to get a reaction :)
        out.write(1);

        while (in.available() > 0) {
            System.out.print(in.read());
        }
        System.out.println("Successfully connected");
    }

}
