.class public test2
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
	putstatic	test2/_runTimer LRunTimer;
	new	PascalTextIn
	dup
	invokenonvirtual	PascalTextIn/<init>()V
	putstatic	test2/_standardIn LPascalTextIn;



.var 0 is main Ljava/lang/StringBuilder;
.var 2 is i I
.var 3 is j I


.line 6
	iconst_0
	istore_2
.line 7
	iconst_1
	istore_3
.line 9
	iload_2
	iload_3
	if_icmpgt	L002
	iconst_0
	goto	L003
L002:
	iconst_1
L003:
	ifeq	L001
	nop
L001:
.line 15
	iconst_0
	istore_3

	getstatic	test2/_runTimer LRunTimer;
	invokevirtual	RunTimer.printElapsedTime()V

	return

.limit locals 4
.limit stack 3
.end method
