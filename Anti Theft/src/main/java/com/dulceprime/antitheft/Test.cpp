#include <iostream>
using namespace std;


int calc();

int main()
{
    int k;

    for(int x =1; x <97; x++)
        {
            if(x==5)
                {
                   break;
                }
            cout << x <<"\n";
            x = x+1 ;
        }

    return 0;
}