package ru.coding4fun.intellij.database.extension

private const val newLine = "\n"
private const val commaWithNewLine = ",\n"

fun StringBuilder.appendLnIfAbsent(): StringBuilder {
	if (this.isEmpty()) return this
	if (!this.endsWith(newLine)) {
		appendJbLn()
	}

	return this
}

fun StringBuilder.appendJbLn(): StringBuilder {
	return append(newLine)
}

private val endsWithGoRegex = Regex("(.+\\s)+GO\\s*$")
fun StringBuilder.appendGo(): StringBuilder {
	if (this.matches(endsWithGoRegex)) return this
	return this.appendLnIfAbsent().append("GO").appendJbLn()
}

fun StringBuilder.addSeparatorScope(separate: (StringBuilder) -> Unit): SeparateScope {
	return SeparateScope(this, "", separate)
}

fun StringBuilder.addSeparatorScope(firstPrefix: String, separate: () -> Unit): SeparateScope {
	return SeparateScope(this, firstPrefix) { separate() }
}

fun StringBuilder.addCommaWithNewLineScope(firstPrefix: String = ""): SeparateScope {
	return SeparateScope(this, firstPrefix) { sb -> sb.append(commaWithNewLine) }
}

class SeparateScope(
	private val stringBuilder: StringBuilder,
	private val firstPrefix: String,
	private val separate: (StringBuilder) -> Unit
) {
	private var myInvokeCount: Int = 0

	val invokeCount get() = myInvokeCount

	fun invoke(appendFun: () -> Unit) {
		separate()
		appendFun()
		increment()
	}

	fun invokeIf(assertion: Boolean, appendFun: () -> Unit): SeparateScopeIfState {
		if (!assertion) return SeparateScopeIfState(false, this)
		invoke(appendFun)
		return SeparateScopeIfState(true, this)
	}

	private fun increment() {
		++myInvokeCount
	}

	private fun separate(): StringBuilder {
		if (myInvokeCount > 0) {
			separate(stringBuilder)
		} else {
			stringBuilder.append(firstPrefix)
		}
		return stringBuilder
	}
}

class SeparateScopeIfState(private val prevAssertion: Boolean, private val separateScope: SeparateScope) {
	fun invokeElseIf(assertion: Boolean, appendFun: () -> Unit): SeparateScopeIfState {
		if (prevAssertion) return SeparateScopeIfState(true, separateScope)
		return separateScope.invokeIf(assertion, appendFun)
	}

	fun invokeElse(appendFun: () -> Unit) {
		if (prevAssertion) return
		separateScope.invoke(appendFun)
	}

	fun invokeIf(assertion: Boolean, appendFun: () -> Unit): SeparateScopeIfState {
		return separateScope.invokeIf(assertion, appendFun)
	}
}