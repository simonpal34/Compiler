.class public test2
.super java/lang/Object

.field private static _runTimer LRunTimer;
.field private static _standardIn LPascalTextIn;

.field private static t I

.method public <init>()V

	aload_0
	invokenonvirtual	java/lang/Object/<init>()V
	return

.limit locals 1
.limit stack 1
.end method

.method private static foo()I

.var 0 is i I

.var 1 is foo I



.line 5
	iconst_1
	istore_0
.line 7
	iload_0
	istore_1

	iload_1
	ireturn

.limit locals 2
.limit stack 1
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	test2/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test2/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i I
.var 3 is j I
.var 4 is c C


.line 13
	bipush	104
	istore	4
.line 17
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%c\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	iload	4
	invokestatic	java/lang/Character.valueOf(C)Ljava/lang/Character;
	aastore
	invokestatic	java/lang/String/format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 18
	iconst_0
	istore	4

	getstatic	test2/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 5
.limit stack 7
.end method
