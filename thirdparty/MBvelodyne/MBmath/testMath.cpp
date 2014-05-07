
#include <iostream>
#include "MBmath.h"

int main(void)
{
	MBmatrix A(3,3);
	MBmatrix B(3,1);
	MBmatrix C(3,1);

	A.name("A");
	B.name("B");
	C.name("C");

	A(0,0) = -10;
	A(1,2) = 1;
	A(2,1) = 2;

	B(0,0) = 1;
	B(1,0) = 2;
	B(2,0) = 3;

	std::cout << std::endl << "Beginning operations." << std::endl;
	C = A*B;

	A.print_stats();
	B.print_stats();
	C.print_stats();

	std::cout << "Done." << std::endl << std::endl;
	return 0;
}