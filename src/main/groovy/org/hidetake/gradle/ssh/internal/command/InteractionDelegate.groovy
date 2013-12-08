package org.hidetake.gradle.ssh.internal.command

import groovy.transform.TupleConstructor
import org.hidetake.gradle.ssh.api.command.Interaction

/**
 * A delegate class for interaction closure.
 *
 * @author hidetake.org
 */
@TupleConstructor
class InteractionDelegate implements Interaction {
    final OutputStream standardInput

    private final List<InteractionRule> interactionRules = []

    @Override
    void when(Map condition, Closure action) {
        interactionRules << InteractionRule.create(condition, action)
    }

    /**
     * Evaluate the closure.
     *
     * @param closure
     * @return interaction rules declared by the closure
     */
    def evaluate(Closure closure) {
        interactionRules.clear()
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        List<InteractionRule> snapshot = []
        snapshot.addAll(interactionRules)
        interactionRules.clear()
        snapshot.asImmutable()
    }
}
