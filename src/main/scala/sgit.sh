#!/bin/sh
exec scala "$0" "$@"
!#


object sgit{
    if($1=="init"){
    Init.main();
    }
}
new sgit();