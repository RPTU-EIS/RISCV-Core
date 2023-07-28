
int main();

__attribute__ ((naked)) void startup(void) __attribute__ (( section(".text_entry") ));

void startup(void)
{
	__asm__ __volatile__(
  "init_stack:"
  "la sp, 0x00003FFC;"
  );

  main();
}

int main(){
  volatile int x1=1;
  volatile int x2=2;
  volatile int y;
  y = x1 + x2;
  return 1;
}