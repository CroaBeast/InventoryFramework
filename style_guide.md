This is the style guide of InventoryFramework, a hard fork of [IF](https://github.com/stefvanschie/IF).
It is an addition to Google's Java Style Guide which you can view [here](https://google.github.io/styleguide/javaguide.html).
All the text underneath are only the points in which the styling of this project differs from Google's Style Guide.

2.3.1 ([original](https://google.github.io/styleguide/javaguide.html#s2.3.1-whitespace-characters)):  
Tab characters are never used for indentation.

3 ([original](https://google.github.io/styleguide/javaguide.html#s3-source-file-structure)):  
This project uses The Unlicense license, therefore no license or copyright information at the top.

3.3.1 ([original](https://google.github.io/styleguide/javaguide.html#s3.3.1-wildcard-imports)):  
Wildcard imports, static or otherwise, *may* be used.

3.3.3 ([original](https://google.github.io/styleguide/javaguide.html#s3.3.3-import-ordering-and-spacing)):  
The import statements do not have to be in ASCII sort order, but import statements starting with the same main package name should be grouped.

4.1.1 ([original](https://google.github.io/styleguide/javaguide.html#s4.1.1-braces-always-used)):  
Braces aren't used when they are a) optional and b) only one statement follows. An `if` whose body is a single
statement on a single line never uses braces, unless it is part of an `if`/`else` chain. This is correct:
```java
if (statement)
    method();
```
while this isn't:
```java
for (statement)
    if (statement)
        method();
```
this should be:
```java
for (statement) {
    if (statement)
        method();
}
```

4.1.3 ([original](https://google.github.io/styleguide/javaguide.html#s4.1.3-braces-empty-blocks)):  
An empty block is immediately opened and closed on the same line as the block construct. This is correct:
```java
void doNothing() {}
```
while this isn't:
```java
void doNothingElse() {
}
```

4.2 ([original](https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation)):  
Indentation uses 4 spaces.

4.4 ([original](https://google.github.io/styleguide/javaguide.html#s4.4-column-limit)):  
The column limit is 120 characters.

4.6.2 ([original](https://google.github.io/styleguide/javaguide.html#s4.6.2-horizontal-whitespace)):  
Point 8: Only the first one is correct, the second one isn't:
```java
//correct
new int[] {5, 6}

//incorrect
new int[] { 5, 6 }
```

4.8.1 ([original](https://google.github.io/styleguide/javaguide.html#s4.8.1-enum-classes)):  
Constants should always be on separate lines and should never be like an array initializer.

4.8.2.1 ([original](https://google.github.io/styleguide/javaguide.html#s4.8.2-variable-declarations)):  
Multiple variable declarations in one line are allowed.

4.8.3.1 ([original](https://google.github.io/styleguide/javaguide.html#s4.8.3-arrays)):  
An array initializer should never be in a matrix like grid. Either all on the same line, or all on separate ones.

4.8.4.2 ([original](https://google.github.io/styleguide/javaguide.html#s4.8.4-switch)):  
Fall through shouldn't be commented.

4.8.5 ([original](https://google.github.io/styleguide/javaguide.html#s4.8.5-annotations)):  
Annotations should always be above the member class/method/field and there should always be one per line. A
nullability annotation comes before a Lombok annotation:
```java
@NotNull
@Getter
private final Slot slot;
```

5.2.4 ([original](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names)):  
Constants are either enum values or fields marked with static and final.

7.1.1 ([original](https://google.github.io/styleguide/javaguide.html#s7.1.1-javadoc-multi-line)):  
A Javadoc should never be on one line. You should always use the first example.

## Additions

**`@since`:** every new public class and member has an `@since` tag with the version of this fork it first appears
in (0.1.0 or later), never a version of the original IF.

**Nullability:** the public API annotates parameters and return values with `@NotNull` and `@Nullable` from
`org.jetbrains.annotations`. `lombok.config` copies them to the members Lombok generates.
`@Contract` is not used.

**`@Override`:** it is only written on members with no other annotation; next to any other annotation it is left out.

**Lombok:** boilerplate that Lombok can generate is never written by hand.
- Accessors are `@Getter` / `@Setter` on the field, named `getX()` / `isX()` / `setX()`.
- Constructors that only assign fields are `@RequiredArgsConstructor` / `@AllArgsConstructor`, with
  `access = AccessLevel.X` when they are not public.
- Classes made of static members only are `@UtilityClass`. Their members are declared without `static` and the class
  has no constructor; nested classes keep their `static` modifier.
- A method is only written by hand when Lombok cannot express it: an override whose name is fixed by an external API,
  a return type deliberately wider than the field, or a body that does more than a plain read or write.
