/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║                 🎯 THE POWER OF TYPESCRIPT LITERAL TYPES                 ║
 * ║          Turn "just strings" into type-safe superpowers! 🚀              ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

// ─────────────────────────────────────────────────────────────────────────────
// 🔴 PROBLEM #1: let vs const - The Great TypeScript Mystery
// ─────────────────────────────────────────────────────────────────────────────

// ❌ With `let` - TypeScript gets lazy:
let day = "Saturday";
// TypeScript says: "Could be anything!" → type: string

// ✅ With `const` - TypeScript goes STRICT:
const day2 = "Saturday";
// TypeScript says: "Locked in!" → type: "Saturday" (literal type!)

console.log("💡 KEY INSIGHT: const activates literal types!");

// ─────────────────────────────────────────────────────────────────────────────
// 🟡 PROBLEM #2: Objects - They Love to Hide Your Type Info
// ─────────────────────────────────────────────────────────────────────────────

// ❌ Without `as const` - Lost in the string void:
let map = { day: "Saturday" };
// type: { day: string } ← Just a string, no specificity

// ✅ With `as const` - Type-safe zone activated:
const map2 = { day: "Saturday" } as const;
// type: { readonly day: "Saturday" } ← LOCKED DOWN! 🔒

console.log("💡 KEY INSIGHT: 'as const' freezes your object types!");

// ─────────────────────────────────────────────────────────────────────────────
// 🟢 PROBLEM #3: Tuples - The Unpredictable Array
// ─────────────────────────────────────────────────────────────────────────────

// ❌ Without `as const` - TypeScript guesses:
const tup = [true, "love"];
// type: (string | boolean)[] ← Could be anything in any order!

// ✅ With `as const` - Exact structure preserved:
const tup2 = [true, "love"] as const;
// type: readonly [true, "love"] ← Exact same structure forever!

console.log("💡 KEY INSIGHT: Tuples become immutable & type-safe!");

// ─────────────────────────────────────────────────────────────────────────────
// 🎓 LEVEL UP: Generics + Literal Types = 🔥 Type Magic
// ─────────────────────────────────────────────────────────────────────────────

// ❌ Boring generic without literal inference:
function randomFunction(param: string) {
  return param;
}

const result1 = randomFunction("hello");
// type: string ← All type info lost!

// ✅ Smart generic WITH literal type preservation:
function randomFunctionWithLiterals<T extends string>(param: T) {
  return param; // Returns exactly what you passed in!
}

const result2 = randomFunctionWithLiterals("hello");
// type: "hello" ← TypeScript REMEMBERS your exact value!

// Now TypeScript catches IMPOSSIBLE comparisons:
if (result2 === "test") {
  // 🚨 ERROR: '"hello"' and '"test"' have no overlap!
  // TypeScript knows result2 can ONLY be "hello"
}

console.log("💡 KEY INSIGHT: Generic type parameters preserve literal types!");

// ─────────────────────────────────────────────────────────────────────────────
// 🏗️ THE REAL POWER: Type-Safe Maps & Records
// ─────────────────────────────────────────────────────────────────────────────

// ❌ Generic object loses literal type info:
function needLiteralMap(foo: Record<string, string>) {
  return foo;
}

const m1 = needLiteralMap({ foo: "foo", bar: "bar" });
// type: Record<string, string> ← All keys are just strings 😞

// ✅ Smart generics + literal types = MAGIC:
function needLiteralMap2<
  T extends string,
  V extends string,
  M extends Record<T, V>,
>(foo: M) {
  return foo; // Returns EXACT shape you passed in!
}

const m2 = needLiteralMap2({ foo: "foo", bar: "bar" });
// type: { foo: "foo"; bar: "bar" } ← Every key & value is known! ✨

console.log("💡 KEY INSIGHT: Preserve object shapes with smart generics!");

// ─────────────────────────────────────────────────────────────────────────────
// 🎯 THE KILLER USE CASE: Type-Safe Feature Flags
// ─────────────────────────────────────────────────────────────────────────────

/**
 * This function ensures:
 * ✅ Handlers MUST match features list
 * ✅ No extra handlers allowed
 * ✅ No missing handlers allowed
 * ✅ All type-safe at compile time!
 */
function makeFeatureFlags<F extends string>(
  flags: F[],
  handlers: { [K in F]: () => void }, // 🔥 Mapped type magic!
) {
  return { flags, handlers };
}

// TypeScript ENFORCES that handlers match flags:
const appFeatures = makeFeatureFlags(["darkMode", "betaDashboard", "newChat"], {
  darkMode: () => console.log("🌙 Dark mode enabled"),
  betaDashboard: () => console.log("🧪 Beta dashboard enabled"),
  newChat: () => console.log("💬 New chat enabled"),
  // extraFeature: () => {}, // ❌ ERROR: Not in flags list!
});

// Result types are PRECISE:
// appFeatures.flags: ("darkMode" | "betaDashboard" | "newChat")[]
// appFeatures.handlers: keys are exactly ["darkMode", "betaDashboard", "newChat"]

console.log("✨ FEATURE FLAGS ARE NOW 100% TYPE-SAFE!");

// ─────────────────────────────────────────────────────────────────────────────
// 📚 THE TAKEAWAY
// ─────────────────────────────────────────────────────────────────────────────

/**
 * 🎯 LITERAL TYPES = Type Safety on STEROIDS
 *
 * Use `as const` for:
 * ✅ Immutable configuration objects
 * ✅ Type-safe feature flags
 * ✅ String literal unions
 * ✅ Preventing impossible comparisons
 *
 * Use generics + `extends string` for:
 * ✅ Preserving exact input shapes
 * ✅ Building type-safe APIs
 * ✅ Mapped types with precision
 * ✅ Eliminate entire categories of bugs!
 *
 * 🚀 Master this, and your TypeScript game changes FOREVER.
 */
