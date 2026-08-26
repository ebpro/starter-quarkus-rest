# Documentation Hub

This folder is organized around **living documentation** with **modular AsciiDoc sources**.

## Point d'entrée principal

- Source canonique: [`living-doc.adoc`](living-doc.adoc)
- Sortie générée: `target/living-docs/living-doc.html`

Structure cible de la page principale:

- Vision (include)
- Liste Epic + NFR (titres + liens)
- Diagrammes globaux (use cases, architecture, domain, technical dependencies)
- Liens vers pages de détail

Additional detail pages:

- Epic details: [`epics/EPIC-01-details.adoc`](epics/EPIC-01-details.adoc)
- NFR details: [`epics/NFR-details.adoc`](epics/NFR-details.adoc)

## Génération

```bash
./mvnw -Pdocs -DskipTests prepare-package
```

## Contenu

- Sections AsciiDoc: [`sections/`](sections/)
- User stories AsciiDoc: [`user-stories/*.adoc`](user-stories/)
- Sources PlantUML: [`uml/`](uml/)
- Epics/NFR: [`epics/`](epics/)

Pages de détail:

- [`epics/EPIC-01-details.adoc`](epics/EPIC-01-details.adoc)
- [`epics/NFR-details.adoc`](epics/NFR-details.adoc)

## Conventions

- `docs/living-doc.adoc` = **source canonique** pour la lecture globale.
- `docs/sections/*.adoc` = **blocs inclus** dans la living doc.
- `docs/user-stories/*.adoc` = **spécifications par cas d’usage** incluses.
- `docs/uml/*.puml` = **sources UML** versionnées.

## Quality Checklist

- Keep user stories aligned with executable BDD features in `src/test/resources/features/catalog/`.
- Keep NFR IDs stable (`NFR-ARCH-*`, `NFR-SEC-*`, `NFR-RELY-*`, `NFR-QA-*`).
- Prefer additive updates (do not rename IDs used by tests, diagrams, or references).
- Regenerate docs after any change and inspect `target/living-docs/living-doc.html`.

## UML Naming Convention

- Epic diagrams: `epic-xx-*.puml`
- User-story diagrams: `us-xxx-*.puml`
- Global diagrams: `global-*.puml`
