#!/usr/bin/perl
#
# The MIT License
# 
# Copyright (c) 2010 tap4j team (see AUTHORS)
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

use strict ;
use Getopt::Long ;
use TAP::Harness ;
use Test::Builder ;


sub TAP::Parser::Aggregator::ok { $_[0]->passed() - ($_[0]->skipped() + $_[0]->todo()) } 


my %METRICS = (
	planned => 'Expected number of planned tests',
	passed 	=> 'Expected number of passed tests (including skip and todo tests)',
	ok  	=> 'Expected number of passed tests (excluding skip and todo tests)', 
	failed	=> 'Expected number of failed tests',
	skipped	=> 'Expected number of skipped tests',
	todo	=> 'Expected number of todo tests',
) ;
my @METRICS = keys %METRICS ;

my %opts = () ;
GetOptions(\%opts, "runtests", "doc", map { "$_:1" } @METRICS) or usage() ;
doc() if $opts{doc} ;
usage() if -t \*STDIN ;

my $harness = new TAP::Harness({verbosity => -3}) ;
my $agg = $harness->runtests([\*STDIN, 'stdin']) ;

my $tb = new Test::Builder() ;
my $tap = '' ;
$tb->output(\$tap) if $opts{runtests} ;
$tb->plan(tests => scalar(@METRICS)) ;
foreach my $m (@METRICS){
	my $result = scalar($agg->$m()) || 0 ;
	my $expected = defined($opts{$m}) ? $opts{$m} : -1 ;
	if ($expected >= 0){
		$tb->is_eq($result, $expected, "$METRICS{$m} == $expected") ;
	}
	else {
		$tb->skip("$METRICS{$m} not specified") ;
	}
}

if (! $opts{runtests}){
	# Just print out the TAP output
	print $tap ;
}
else {
	# Send it through the harness!
	open(MEM, '<', \$tap) ;
	TAP::Harness->new()->runtests([\*MEM, 'TAP']) ;
	close(MEM) ;
}


#################################################


sub usage {
	my $more = shift ;
	print STDERR <<USAGE;
Usage: $0 OPTION...

metatap is a simple testing tool for TAP producers. It reads TAP from standard 
input, along with the expected results on the command line. It then produces 
(and optionally runs) TAP describing how the input TAP was interpreted by 
TAP::Harness.

USAGE
	print STDERR 
		(map { sprintf("  --%-10s $METRICS{$_}\n", "$_:") } @METRICS),
		"  --runtests:  Run the produced TAP though TAP::Harness\n" ;
		"  --doc:       Print a usage example\n" ;
	exit 1 unless $more ;
}


sub doc {
	usage(1) ;
	print STDERR <DATA> ;
	exit 1 ;
}


__DATA__

For example, if you send to following TAP:

  1..5
  ok 1 - 1 equals 1
  ok 2 - 1 equals 1
  ok 3 # skip because
  not ok 4 - 1 equals 2
  ok 5 - 1 == 2 # TODO because

into

  metatap --planned=5 --ok=2 --failed=1 --skip=1 --todo=0

it will produce the following (TAP) output:

  1..6
  ok 1 - Expected number of passed tests (excluding skip and toto tests) == 2
  ok 2 - Expected number of planned tests == 5
  ok 3 # skip Expected number of passed tests (including skip and toto tests) not specified
  ok 4 - Expected number of failed tests == 1
  not ok 5 - Expected number of todo tests == 0
  ok 6 - Expected number of skipped tests == 1

adding the --runtests, the produced TAP will be run in a TAP::Harness, yielding:

  TAP .. Failed 1/6 subtests 
          (less 1 skipped subtest: 4 okay)

  Test Summary Report
  -------------------
  TAP (Wstat: 0 Tests: 6 Failed: 1)
    Failed test:  5
  Files=1, Tests=6,  0 wallclock secs ( 0.00 usr +  0.00 sys =  0.00 CPU)
  Result: FAIL
