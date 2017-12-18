.class public test3
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;


.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	test3/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test3/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i F
.var 3 is j I


.line 6
	getstatic	test3/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.readReal()F
	fstore_2
	getstatic	test3/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.nextLine()V
.line 7
	getstatic	test3/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.readInteger()I
	istore_3
	getstatic	test3/_standardIn LPascalTextIn;
	invokevirtual	PascalTextIn.nextLine()V
.line 7
	iload_3
	iconst_5
	imul
	i2f
	fload_2
	iconst_2
	i2f
	fdiv
	iconst_2
	i2f
	fdiv
	fadd
	fstore_2
.line 11
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	fload_2
	invokestatic	java/lang/Float.valueOf(F)Ljava/lang/Float;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 12
	iconst_0
	istore_3

	getstatic	test3/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 4
.limit stack 7
.end method
