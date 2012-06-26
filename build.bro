{project sample "A sample project"
  [macro (object-file name)
    (let ((target-name (string->symbol (string-append "compile-" name "-o")))
          (source-name (string-append name ".c"))
          (object-name (string-append name ".o")))
      `(target ,target-name
         (depends ,source-name) (creates ,object-name)
         (! ,(string-append "gcc -o " object-name " " source-name))))
  ]
  [target compile (depends "main.c") (creates "build/foo")
    (! "gcc -o foo main.c")
    (! "mkdir -p build")
    (mv "foo" "build/foo")
  ]
  [target run (depends compile) (creates)
    (! "./build/foo")
  ]
}
