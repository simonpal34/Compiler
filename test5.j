.class public test5
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
	putstatic	test5/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test5/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i I
.var 3 is j I


.line 6
	iconst_0
	istore_2
.line 7
	iconst_5
	istore_3
.line 10
L001:
	iload_2
	iload_3
	if_icmplt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	iconst_1
	ixor
	ifne	L002
.line 11
	iload_2
	iconst_1
	iadd
	istore_2
.line 13
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload_2
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
	goto	L001
L002:
.line 15
	iconst_0
	istore_3

	getstatic	test5/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 4
.limit stack 7
.end method
