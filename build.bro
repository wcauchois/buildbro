{project sample "A sample project"
  [target compile (depends "main.c") (creates "build/foo")
    (! "gcc -o foo main.c")
    (! "mkdir -p build")
    (mv "foo" "build/foo")
  ]
  [target run (depends compile) (creates)
    (! "./build/foo")
  ]
}
