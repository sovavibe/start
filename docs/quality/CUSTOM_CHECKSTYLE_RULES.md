# Custom Checkstyle Rules

## Overview

This document describes how to create and use custom Checkstyle rules for project-specific checks:
- **FQN (Fully Qualified Names)** - Detects usage of fully qualified class names
- **Non-ASCII Characters** - Detects non-ASCII characters in code (excluding emojis and comments)

## Professional Solutions

### Option 1: Custom Checkstyle Check (Recommended)

**Pros:**
- ✅ Full control over detection logic
- ✅ Integrates seamlessly with existing Checkstyle setup
- ✅ Can be version-controlled and shared
- ✅ Works with Baseline (doesn't override defaults)

**Cons:**
- ⚠️ Requires Java development
- ⚠️ Needs maintenance

### Option 2: RegexpSinglelineJava (Simple)

**Pros:**
- ✅ No Java code needed
- ✅ Quick to implement

**Cons:**
- ⚠️ Limited pattern matching
- ⚠️ Can have false positives
- ⚠️ Harder to maintain complex patterns

### Option 3: AI Rules + Code Review (Current)

**Pros:**
- ✅ No maintenance overhead
- ✅ Flexible and context-aware
- ✅ Already working

**Cons:**
- ⚠️ Not automatic (requires AI/developer attention)

## Implementation: Custom Checkstyle Check

### Step 1: Create Custom Check Class

Create a custom Checkstyle check in `buildSrc`:

```java
package com.digtp.start.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Custom Checkstyle check to detect fully qualified names (FQN) in code.
 * Example violations:
 * - io.jmix.flowui.view.View<?> loginView → should use View<?> with import
 * - java.lang.reflect.Method method → should use Method with import
 */
public class AvoidFullyQualifiedNamesCheck extends AbstractCheck {
    
    private static final String MSG_KEY = "avoid.fully.qualified.names";
    
    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.IDENT};
    }
    
    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }
    
    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        final String text = ast.getText();
        
        // Check if identifier contains dots (FQN pattern)
        // Pattern: at least 2 dots (package.subpackage.Class)
        if (text != null && countDots(text) >= 2) {
            // Exclude imports and package declarations
            if (!isInImportOrPackage(ast)) {
                log(ast, MSG_KEY, text);
            }
        }
    }
    
    private int countDots(String text) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == '.') {
                count++;
            }
        }
        return count;
    }
    
    private boolean isInImportOrPackage(DetailAST ast) {
        DetailAST parent = ast.getParent();
        while (parent != null) {
            if (parent.getType() == TokenTypes.IMPORT 
                    || parent.getType() == TokenTypes.IMPORT_STATIC
                    || parent.getType() == TokenTypes.PACKAGE_DEF) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}
```

### Step 2: Create Non-ASCII Characters Check

```java
package com.digtp.start.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Custom Checkstyle check to detect non-ASCII characters in code.
 * Excludes:
 * - Comments (single-line and multi-line)
 * - String literals (for internationalization)
 * - Emojis (common Unicode ranges)
 */
public class NonAsciiCharactersCheck extends AbstractCheck {
    
    private static final String MSG_KEY = "non.ascii.characters";
    
    // Unicode ranges for common emojis (excluded from check)
    private static final String EMOJI_PATTERN = 
        "[\u1F300-\u1F9FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u1F600-\u1F64F]|[\u1F680-\u1F6FF]";
    
    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.IDENT, TokenTypes.LITERAL_STRING};
    }
    
    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }
    
    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        final String text = ast.getText();
        
        if (text != null && containsNonAscii(text)) {
            // Check if it's in a comment (exclude)
            if (!isInComment(ast)) {
                // Check if it's an emoji (exclude)
                if (!isEmoji(text)) {
                    log(ast, MSG_KEY, text);
                }
            }
        }
    }
    
    private boolean containsNonAscii(String text) {
        for (char c : text.toCharArray()) {
            if (c > 127) { // Non-ASCII
                return true;
            }
        }
        return false;
    }
    
    private boolean isEmoji(String text) {
        return text.matches(".*" + EMOJI_PATTERN + ".*");
    }
    
    private boolean isInComment(DetailAST ast) {
        final FileContents contents = getFileContents();
        final int lineNo = ast.getLineNo();
        final String line = contents.getLine(lineNo);
        
        // Check for single-line comment
        if (line != null && line.trim().startsWith("//")) {
            return true;
        }
        
        // Check for multi-line comment (simplified)
        // Full implementation would need to track comment blocks
        return false;
    }
}
```

### Step 3: Register Custom Checks

Add to `buildSrc/build.gradle`:

```gradle
dependencies {
    implementation 'com.puppycrawl.tools:checkstyle:10.12.5'
}
```

### Step 4: Configure in checkstyle.xml

Add to `.baseline/checkstyle/checkstyle.xml` (in TreeWalker section):

```xml
<module name="com.digtp.start.checkstyle.AvoidFullyQualifiedNamesCheck">
    <property name="id" value="AvoidFullyQualifiedNames"/>
    <message key="avoid.fully.qualified.names" 
             value="Avoid fully qualified names. Use imports instead: ''{0}''"/>
</module>

<module name="com.digtp.start.checkstyle.NonAsciiCharactersCheck">
    <property name="id" value="NonAsciiCharacters"/>
    <message key="non.ascii.characters" 
             value="Avoid non-ASCII characters in code (excluding comments and strings): ''{0}''"/>
</module>
```

## Alternative: RegexpSinglelineJava (Simpler)

For a simpler solution without Java code, use `RegexpSinglelineJava`:

### FQN Check

```xml
<module name="RegexpSinglelineJava">
    <property name="format" value="\b[a-z][a-zA-Z0-9]*\.[a-z][a-zA-Z0-9]*\.[a-zA-Z0-9]+\b"/>
    <property name="message" value="Avoid fully qualified names. Use imports instead."/>
    <property name="ignoreComments" value="true"/>
    <property name="ignoreStrings" value="true"/>
</module>
```

### Non-ASCII Check

```xml
<module name="RegexpSinglelineJava">
    <property name="format" value="[^\x00-\x7F]"/>
    <property name="message" value="Avoid non-ASCII characters in code."/>
    <property name="ignoreComments" value="true"/>
    <property name="ignoreStrings" value="true"/>
</module>
```

## Recommendation

For this project, **Option 3 (AI Rules + Code Review)** is recommended because:
1. ✅ Already working and integrated
2. ✅ No maintenance overhead
3. ✅ Flexible and context-aware
4. ✅ Follows project principles (Vibe Coding)

If automatic enforcement is required, use **Option 1 (Custom Checkstyle Check)** for better control and accuracy.

