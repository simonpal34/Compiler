int foo (int n, float f){
    int k;
    k = 1 + 3;
    char c;
    c  = 'h';
    return k;
}
int main (){
 
int i;
float j;
int f;
i = 1; // whatever value is ok
j= 4; // whatever value is ok

if(i == 1)
{
    j = j / 1;
};
f = foo(i,j);
writeln(i);
writeln(f);
return 0;
}
