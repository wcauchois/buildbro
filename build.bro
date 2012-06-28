{project sample "A sample project"
  [macro (object-file name)
    (let ((target-name (string->symbol (string-append "compile-" name)))
          (source-name (string-append name ".c"))
          (object-name (string-append name ".o")))
      `(target ,target-name
         (depends ,source-name) (creates ,object-name)
         (! ,(string-append "gcc -c " source-name))))
  ]
  (object-file "main")
  (object-file "lib")
  [target compile (depends "compile-main" "compile-lib") (creates "foo")
    (! "gcc -o foo main.o lib.o")
  ]
  [target run (depends compile) (creates)
    (! "./foo")
  ]
}
