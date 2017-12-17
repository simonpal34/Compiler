.class public test1
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

.method private static foo(IF)I

.var 0 is n I
.var 1 is f F
.var 2 is k I
.var 3 is c C

.var 4 is foo I



.line 3
	iconst_1
	iconst_3
	iadd
	istore_2
.line 5
	bipush	104
	istore_3
.line 7
	iload_2
	istore	4

	iload	4
	ireturn

.limit locals 5
.limit stack 2
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	test1/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test1/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i I
.var 3 is j F
.var 4 is f I


.line 13
	iconst_1
	istore_2
.line 14
	ldc	4.0
	fstore_3
.line 20
	iload_2
	iconst_1
	if_icmpeq	L002
	iconst_0
	goto	L003
L002:
	iconst_1
L003:
	ifeq	L001
.line 18
	fload_3
	iconst_1
	i2f
	fdiv
	fstore_3
L001:
.line 20
	iload_2
	fload_3
	invokestatic	test1/foo(IF)I
	istore	4
.line 22
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
.line 23
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%d\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload	4
	invokestatic	java/lang/Integer.valueOf(I)Ljava/lang/Integer;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 24
	iconst_0
	istore	4

	getstatic	test1/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 5
.limit stack 7
.end method
