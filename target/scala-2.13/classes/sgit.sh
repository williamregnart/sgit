#!/bin/sh
exec scala "$0" "$@"
!#

import Init

object sgit{
    if($1=="init"){
    new Init().main();
    }
}
new HelloWorld();