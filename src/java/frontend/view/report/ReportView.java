/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package frontend.view.report;

import com.hp.hpl.jena.query.ResultSetRewindable;
import frontend.controller.resource.report.ReportResource;
import frontend.view.FrontEndView;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import model.sdb.SDB;
import view.JSONSerializer;
import view.QueryResult;
import view.QueryStringBuilder;
import view.XMLSerializer;

/**
 *
 * @author Pumba
 */
abstract public class ReportView extends FrontEndView
{
    private ResultSetRewindable queryResults = null;

    public ReportView(ReportResource resource)
    {
	super(resource);
    }

    @Override
    public ReportResource getResource()
    {
	return (ReportResource)super.getResource();
    }

    @Override
    public void display(HttpServletRequest request, HttpServletResponse response) throws IOException, TransformerException, ParserConfigurationException
    {
	// to be moved out!!!
        setBindingTypes(QueryResult.select(SDB.getDataset(), QueryStringBuilder.build(getController().getServletContext().getResourceAsStream("/WEB-INF/sparql/ontology/binding-types.rq"))));

        super.display(request, response);
    }

    public ResultSetRewindable getQueryResults()
    {
        return queryResults;
    }

    public void setQueryResults(ResultSetRewindable queryResults)
    {
        this.queryResults = queryResults;
    }

    /*
    protected void setQueryResults(ResultSetRewindable results)
    {
	getTransformer().setParameter("query-result", true);

	getResolver().setArgument("results", XMLSerializer.serialize(results));
    }
    */

    protected void setReport(ResultSetRewindable report)
    {
        setDocument(XMLSerializer.serialize(report));
        getResolver().setArgument("report", XMLSerializer.serialize(report));
    }

    protected void setEndpoints(ResultSetRewindable endpoints)
    {
	getResolver().setArgument("endpoints", XMLSerializer.serialize(endpoints));
    }

    protected void setVisualizations(ResultSetRewindable visualizations)
    {
        getResolver().setArgument("visualizations", XMLSerializer.serialize(visualizations));
	getTransformer().setParameter("visualizations-json", JSONSerializer.serialize(visualizations));
    }

    protected void setBindings(ResultSetRewindable bindings)
    {
	getResolver().setArgument("bindings", XMLSerializer.serialize(bindings));
	getTransformer().setParameter("bindings-json", JSONSerializer.serialize(bindings));
    }

    protected void setVariables(ResultSetRewindable variables)
    {
        getResolver().setArgument("variables", XMLSerializer.serialize(variables));
	getTransformer().setParameter("variables-json", JSONSerializer.serialize(variables));
    }

    protected void setOptions(ResultSetRewindable options)
    {
        getResolver().setArgument("options", XMLSerializer.serialize(options));
    }

    protected void setVisualizationTypes(ResultSetRewindable visTypes)
    {
	getResolver().setArgument("visualization-types", XMLSerializer.serialize(visTypes));
	getTransformer().setParameter("visualization-types-json", JSONSerializer.serialize(visTypes));
    }

    protected void setBindingTypes(ResultSetRewindable bindingTypes)
    {
	getResolver().setArgument("binding-types", XMLSerializer.serialize(bindingTypes));
	getTransformer().setParameter("binding-types-json", JSONSerializer.serialize(bindingTypes));
    }

    protected void setDataTypes(ResultSetRewindable dataTypes)
    {
	getResolver().setArgument("data-types", XMLSerializer.serialize(dataTypes));
	getTransformer().setParameter("data-types-json", JSONSerializer.serialize(dataTypes));
    }

}
