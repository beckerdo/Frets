Frets
==========
Frets is an application to help find guitar chords, variations, and patterns.

Using Frets, one can construct a fretboard of any number of strings, frets, and tuning.
Then the fretboard can convert chord formulas into locations on the fretboard.
Permutations of the chord formula can be found and the locations can be ranked
according to fret spans, string skips, proximity, etc.

Frets is intended as a toolkit or dependency to be incorporated into graphical
and other musical programs


Demonstration
==========
Run the unit tests (especially LocationListTest) to see how you can find
inversions, variations, and transpositions, and perform sorts and rankings. Some
of the unit tests are left verbose to show the functions.

Frets has rudimentary, character-based fretboard rendering for displaying notes 
and fretboards. The displays have many options such as vertical or horizontal
orientation, right or left hand view, finger or note display, etc.

TODOs
==========
1. Break up ChordRank into individual and composite rankers. All ranks based on 0-100.
2. Add fretboard Viewport so that character and graphics renderings can be auto or specified. 
  (ALL, AUTO, MANUAL)

Contributors
==========
   <a href="mailto:dan@danbecker.info">Dan Becker</a>