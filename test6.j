.class public test6
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

.method private static foo()I


.var 1 is foo I



.line 3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"f\n"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 4
	iconst_1
	istore_1

	iload_1
	ireturn

.limit locals 2
.limit stack 2
.end method

.method private static bar()I


.var 1 is bar I



.line 7
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"b\n"
	invokevirtual	java/io/PrintStream.print(Ljava/lang/String;)V
.line 8
	iconst_1
	istore_1

	iload_1
	ireturn

.limit locals 2
.limit stack 2
.end method

.method public static main([Ljava/lang/String;)V

	new	RunTimer
	dup
	invokenonvirtual	RunTimer/<init>()V
	putstatic	test6/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test6/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i I


.line 11
	invokestatic	test6/foo()I
	istore_2
.line 12
	invokestatic	test6/bar()I
	istore_2
.line 13
	invokestatic	test6/foo()I
	istore_2
.line 16
	iconst_1
	istore_2

	getstatic	test6/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 3
.limit stack 3
.end method
