package com.railway.reservation.analysis;

import aj.org.objectweb.asm.ClassReader;
import aj.org.objectweb.asm.ClassVisitor;
import aj.org.objectweb.asm.MethodVisitor;
import aj.org.objectweb.asm.Opcodes;

import java.util.Set;

public class IOAnalyzer {

    private static final Set<String> knownIOLibraries = Set.of(
        "java/io", "java/nio/file", "java/net", "java/net/http",
        "javax/mail", "javax/persistence", "org/postgresql",
        "org/springframework/data", "org/springframework/mail",
        "org/springframework/web/client", "org/springframework/kafka",
        "org/apache/commons/fileupload", "com/amazonaws", "com/google/firebase"
    );

    public static boolean isIOBound(String className) {
        try {
            final boolean[] ioFlag = {false};

            ClassReader reader = new ClassReader(className);
            reader.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (!name.equals("runTask")) return null;
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            for (String libPrefix : knownIOLibraries) {
                                if (owner.startsWith(libPrefix)) {
                                    ioFlag[0] = true;
                                    break;
                                }
                            }
                        }
                    };
                }
            }, 0);

            return ioFlag[0];
        } catch (Exception e) {
            System.err.println("⚠️ ASM analysis failed for: " + className + " => " + e.getMessage());
            return false;
        }
    }
}
