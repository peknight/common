/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2027 PeKnight(JKpeknight@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.peknight.common.net;

import java.net.InetAddress;
import java.net.Socket;

/**
 *
 *
 * @author PeKnight
 *
 * Created by PeKnight on 2017/10/9.
 */
public final class SocketUtils {
    private SocketUtils() {}

    public static String getRemoteHost(Socket socket) {
        String remoteHost = "?";
        if (socket != null) {
            try {
                InetAddress remoteAddress = socket.getInetAddress();
                if (remoteAddress != null) {
                    String temp = remoteAddress.toString();
                    if (temp.startsWith("/")) {
                        remoteHost = temp.substring(1);
                    }
                }
            } catch (NullPointerException e) {}
        }
        return remoteHost;
    }

    public static int getRemotePort(Socket socket) {
        int remotePort = -1;
        if (socket != null) {
            try {
                remotePort = socket.getPort();
            } catch (NullPointerException e) {}
        }
        return remotePort;
    }

    public static int getLocalPort(Socket socket) {
        int localPort = -1;
        if (socket != null) {
            try {
                localPort = socket.getLocalPort();
            } catch (NullPointerException e) {}
        }
        return localPort;
    }


    public static String socketInfo(int localPort, String remoteHost, int remotePort) {
        return localPort + " <=> " + remoteHost + ":" + remotePort;
    }

    public static String socketInfo(Socket socket, String remoteHost, int remotePort) {
        return socketInfo(getLocalPort(socket), remoteHost, remotePort);
    }

    public static String socketInfo(Socket socket) {
        return socketInfo(getLocalPort(socket), getRemoteHost(socket), getRemotePort(socket));
    }
}
