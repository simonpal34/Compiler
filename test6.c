int foo(){
writeln('f');
return 1;
}
int bar(){
    writeln('b');
 return 1;
}
int main(){
 int i;
i = foo();
i = bar();
i  = foo();
 
 return 1;
}