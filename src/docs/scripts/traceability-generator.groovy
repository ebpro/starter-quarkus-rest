import groovy.json.JsonSlurper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ─────────────────────────────────────────────────────────────
// CONFIG
// ─────────────────────────────────────────────────────────────

def cucumberJson = new File("${project.basedir}/target/cucumber-reports/cucumber.json")
def outDir = new File("${project.basedir}/target/generated-docs")
outDir.mkdirs()

def tsFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
def dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

def nowTs = LocalDateTime.now().format(tsFmt)
def nowDate = LocalDateTime.now().format(dateFmt)

// ─────────────────────────────────────────────────────────────
// SAFE GUARD
// ─────────────────────────────────────────────────────────────

if (!cucumberJson.exists()) {
    println "[traceability] WARNING: cucumber.json not found — generating placeholder"

    def empty = """\
= Traceability Matrix

[WARNING]
====
No Cucumber test results found.
Run `mvn verify -Pdocs` to generate a full matrix.
====
"""

    new File(outDir, "_traceability-table.adoc").text = empty
    return
}

// ─────────────────────────────────────────────────────────────
// PARSE JSON (defensive)
// ─────────────────────────────────────────────────────────────

def parser = new JsonSlurper()
def features = parser.parse(cucumberJson)

// normalize to avoid surprises from malformed JSON
if (!(features instanceof List)) {
    throw new IllegalStateException("Expected Cucumber JSON array but got: ${features?.getClass()}")
}

// ─────────────────────────────────────────────────────────────
// MODEL
// ─────────────────────────────────────────────────────────────

class ScenarioRow {
    String epic
    String usId
    String scenario
    String feature
    String status
    String icon
}

// deterministic map (sorted)
def matrix = new TreeMap<String, List<ScenarioRow>>().withDefault { [] }

// ─────────────────────────────────────────────────────────────
// TRANSFORM
// ─────────────────────────────────────────────────────────────

features.each { feature ->

    def featureName = feature?.name ?: "UNKNOWN_FEATURE"
    def elements = feature?.elements ?: []

    elements.each { scenario ->

        if (!scenario || scenario.type == 'background') return

        def tags = (scenario.tags ?: [])
                .collect { it?.name ?: "" }
                .collect { it.replace('@', '') }

        def usId = tags.find { it.startsWith('US-') } ?: 'UNTAGGED'
        def epicId = tags.find { it.startsWith('EPIC-') } ?: '-'

        def steps = scenario?.steps ?: []

        def failed = steps.any { it?.result?.status == 'failed' }
        def skipped = steps && steps.every { it?.result?.status == 'skipped' }

        def status =
                failed  ? 'FAILED' :
                skipped ? 'SKIPPED' :
                          'PASSED'

        def icon =
                failed  ? 'icon:times-circle[role=red]' :
                skipped ? 'icon:question-circle[role=gray]' :
                          'icon:check-circle[role=green]'

        def row = new ScenarioRow(
                epic: epicId,
                usId: usId,
                scenario: scenario?.name ?: "Unnamed Scenario",
                feature: featureName,
                status: status,
                icon: icon
        )

        matrix[usId] << row
    }
}

// ─────────────────────────────────────────────────────────────
// BUILD ASCIIDOC
// ─────────────────────────────────────────────────────────────

def sb = new StringBuilder(16_384)

sb << """\
// AUTO-GENERATED — DO NOT EDIT
// Generator: traceability-generator.groovy (SOTA 2026)
// Timestamp: ${nowTs}

= Traceability Matrix

[cols="1,1,4,1",options="header",stripes=even]
|===
|Epic |User Story |Scenario |Status

"""

matrix.each { usId, rows ->
    rows.each { r ->
        sb << "|${r.epic}\n|${r.usId}\n|${r.scenario}\n|${r.icon}\n\n"
    }
}

sb << "|===\n\n"

// ─────────────────────────────────────────────────────────────
// METRICS
// ─────────────────────────────────────────────────────────────

def all = matrix.values().flatten()

def total = all.size()
def passed = all.count { it.status == 'PASSED' }
def failed = all.count { it.status == 'FAILED' }
def skipped = all.count { it.status == 'SKIPPED' }

def pct = total > 0 ? ((passed * 100.0) / total).round(1) : 0.0

// ─────────────────────────────────────────────────────────────
// SUMMARY BLOCK
// ─────────────────────────────────────────────────────────────

sb << """\
[NOTE]
====
*Coverage summary* — ${nowDate} — version `${project.version}` +

icon:check-circle[role=green] *${passed} passed* |
icon:times-circle[role=red] *${failed} failed* |
icon:question-circle[role=gray] *${skipped} skipped* |

*Total:* ${total} scenarios |
*Pass rate:* ${pct}%
====
"""

// ─────────────────────────────────────────────────────────────
// WRITE OUTPUT
// ─────────────────────────────────────────────────────────────

def out = new File(outDir, "_traceability-table.adoc")
out.text = sb.toString()

println "[traceability] Matrix written → ${out.path} (total=${total}, passed=${passed}, failed=${failed})"

// ─────────────────────────────────────────────────────────────
// BUILD FAILURE POLICY
// ─────────────────────────────────────────────────────────────

if (failed > 0) {
    throw new RuntimeException(
            "[traceability] BUILD BLOCKED: ${failed} failing scenario(s)"
    )
}
