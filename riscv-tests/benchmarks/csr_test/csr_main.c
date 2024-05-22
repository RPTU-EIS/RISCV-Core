// See LICENSE for license details.

//**************************************************************************
// Median filter bencmark
//--------------------------------------------------------------------------
//
// This benchmark performs a 1D three element median filter. The
// input data (and reference data) should be generated using the
// median_gendata.pl perl script and dumped to a file named
// dataset1.h.

#include "util.h"
#include <stdio.h>


//--------------------------------------------------------------------------
// Input/Reference Data

//--------------------------------------------------------------------------
// Main

int main( int argc, char* argv[] )
{
   uint64_t ustatus_write = 1;
   uint64_t ustatus_read = 0;
   __asm__ __volatile__ ("csrr %0, ustatus": "=r" (ustatus_read));
   printf("Read ustatus: %x \n", ustatus_read); 
   write_csr(ustatus, ustatus_write); 
   __asm__ __volatile__ ("csrr %0, ustatus": "=r" (ustatus_read));
   printf("Read ustatus: %x \n", ustatus_read); 
   
  

  // Check the results
  return 0;
}
