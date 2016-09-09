package ru.sbt.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class ServerRegistrator {
    public static void listen(int port, Object impl) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = new ServerSocket(port);
        try (Socket client = serverSocket.accept()) {
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());

            String methodName = getMethodName(inputStream);
            Object[] args = (Object[]) inputStream.readObject();

            Method method = impl.getClass().getMethod(methodName, Stream.of(args).map(Object::getClass).toArray(Class[]::new));
            try {
                Object result = method.invoke(impl, args);
                outputStream.writeBoolean(false);
                outputStream.writeObject(result);
            }catch(Exception e) {
                outputStream.writeBoolean(true);
                outputStream.writeObject(e);
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ServerRegistrator.listen(5000, new CalculatorImpl());
    }

    private static String getMethodName(InputStream stream) throws IOException {
        byte[] bytes = new byte[1024];
        int count = stream.read(bytes);
        return new String(bytes, 0, count);
    }
}