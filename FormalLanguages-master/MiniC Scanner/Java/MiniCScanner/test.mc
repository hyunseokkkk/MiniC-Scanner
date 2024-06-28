/* 
   A palindromic number is unchanged if its digits are reversed.
   121 or 1221 is a palindrome.
*/

const max = 100;

int sum(int n, int m)
{
   int i;
   
   write(n);
   write(m);
   i = n + m + max;
   if (i == 1) i = 100;
   return i;
}
   
void main()
{
   double x;
   string a;
   char b;

   a = "asdfasdf";
   b = 'a';

   write(max);
   x = .312321;
   x++;
   write(x);
   x = sum(10, 20);
   write(x);
}
