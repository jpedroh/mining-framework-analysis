/*
 * generated by Xtext
 */
package org.sqlproc.dsl.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.outline.impl.OutlineFilterAndSorter;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.sqlproc.dsl.resolver.PojoResolver;
import org.sqlproc.dsl.resolver.PojoResolverFactory;
import org.sqlproc.dsl.resolver.PojoResolverFactoryBean;
import org.sqlproc.dsl.ui.outline.FilterMappingRulesContribution;
import org.sqlproc.dsl.ui.outline.FilterMetaStatementsContribution;
import org.sqlproc.dsl.ui.outline.FilterOptionalFeaturesContribution;
import org.sqlproc.dsl.ui.outline.FixedOutlineFilterAndSorter;
import org.sqlproc.dsl.ui.resolver.WorkspacePojoResolverImpl;
import org.sqlproc.dsl.ui.syntaxcoloring.HighlightingConfiguration;
import org.sqlproc.dsl.ui.syntaxcoloring.SemanticHighlightingCalculator;
import org.sqlproc.dsl.ui.syntaxcoloring.TokenToIdMapper;
import org.sqlproc.dsl.ui.templates.ProcessorDslTemplateContextType;
import org.sqlproc.dsl.ui.templates.ProcessorTemplateProposalProvider;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class ProcessorDslUiModule extends org.sqlproc.dsl.ui.AbstractProcessorDslUiModule {

    public ProcessorDslUiModule(AbstractUIPlugin plugin) {
        super(plugin);
    }

    public Class<? extends PojoResolverFactory> bindPojoResolverFactory() {
        return PojoResolverFactoryBean.class;
    }

    public Class<? extends PojoResolver> bindPojoResolver() {
        return WorkspacePojoResolverImpl.class;
    }

    public Class<? extends IHighlightingConfiguration> bindISemanticHighlightingConfiguration() {
        return HighlightingConfiguration.class;
    }

    public Class<? extends DefaultAntlrTokenToAttributeIdMapper> bindDefaultAntlrTokenToAttributeIdMapper() {
        return TokenToIdMapper.class;
    }

    public Class<? extends ISemanticHighlightingCalculator> bindISemanticHighlightingCalculator() {
        return SemanticHighlightingCalculator.class;
    }

    public Class<? extends OutlineFilterAndSorter> bindOutlineFilterAndSorter() {
        return FixedOutlineFilterAndSorter.class;
    }

    public void configureFilterOptionalFeaturesOutlineContribution(Binder binder) {
        binder.bind(IOutlineContribution.class).annotatedWith(Names.named("FilterOptionalFeaturesContribution"))
                .to(FilterOptionalFeaturesContribution.class);
    }

    public void configureFilterMetaStatementsOutlineContribution(Binder binder) {
        binder.bind(IOutlineContribution.class).annotatedWith(Names.named("FilterMetaStatementsContribution"))
                .to((Class<? extends IOutlineContribution>) FilterMetaStatementsContribution.class);
    }

    public void configureFilterMappingRulesOutlineContribution(Binder binder) {
        binder.bind(IOutlineContribution.class).annotatedWith(Names.named("FilterMappingRulesContribution"))
                .to(FilterMappingRulesContribution.class);
    }

    @Override
    public void configure(Binder binder) {
        super.configure(binder);
        binder.bind(XtextTemplateContextType.class).to(ProcessorDslTemplateContextType.class);
    }

    @Override
    public Class<? extends ITemplateProposalProvider> bindITemplateProposalProvider() {
        return ProcessorTemplateProposalProvider.class;
    }
}
