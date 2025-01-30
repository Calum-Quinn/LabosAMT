package ch.heigvd.amt.inspector;

import java.lang.reflect.*;

public class Inspector {

    /**
     * Inspect an object and print its class name, fields and methods.
     * @param object the object to inspect
     */
    public static void inspect(Object object) {
        // TODO: implement this method to inspect an object and print its class name, fields and methods
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        System.out.println("Class name is : " + clazz.getName());
        System.out.println("Fields are:");
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                System.out.println("- " + field.getName() + " : " + field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Methods are:");
        for (Method method : methods) {
            System.out.println("- " + method.getName());
        }
    }

    /**
     * Demonstrate the use of the Inspector class by inspecting a dog.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        inspect(new Dog("Buddy", 5));
    }
}
