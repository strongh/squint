# squint

Converts images to ASCII art. Splits images into boxes of pixels, calculates mean brightness of box, and assigns a character based on a hardcoded mapping.


## Usage

```
lein run <filename> [<pixels-per-character>]
```

Note that currently the file must reside on the classpath.

Output is saved to `<filename>.txt` in working directory.

Try running on included PNG:

```
lein run sample.png 10
```

## TODO

+ support usage as library
+ tests
+ better command-line usage (e.g. output file name)
+ more options for sizing output
+ options for calculating brightness
+ options for sampling/calculating brightness
+ normalize brightness before assigning characters
+ configure brightness->character mapping
+ optionally use ANSI colors?
+ match character shapes to boxes of pixels

## License

Copyright © 2014 Homer Strong

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
