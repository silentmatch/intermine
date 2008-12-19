package org.intermine.bio.web.logic;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMessage;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.BagConverter;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.pathqueryresult.PathQueryResultHelper;
import org.intermine.web.logic.query.WebResultsExecutor;
import org.intermine.web.logic.results.WebResults;
import org.intermine.web.logic.session.SessionMethods;

/**
 * @author "Xavier Watkins"
 *
 */
public class OrthologueConverter implements BagConverter
{

    /**
     * The Constructor
     */
    public OrthologueConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public WebResults getConvertedObjects (HttpSession session, String organism,
                                      List<Integer> fromList, String type)
                                      throws ObjectStoreException {
        ServletContext servletContext = session.getServletContext();
        Model model = ((ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE)).getModel();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);

        PathQuery pathQuery = new PathQuery(model);
        List<Path> view = PathQueryResultHelper.getDefaultView(type, model, webConfig,
                        "Gene.homologues.homologue", false);
        pathQuery.setViewPaths(view);
        String label = null, id = null, code = pathQuery.getUnusedConstraintCode();
        List<InterMineObject> objectList = os.getObjectsByIds(fromList);
        Constraint c = new Constraint(ConstraintOp.IN, objectList,
                                        false, label, code, id, null);
        pathQuery.addNode(type).getConstraints().add(c);

        code = pathQuery.getUnusedConstraintCode();
        Constraint c2 = new Constraint(ConstraintOp.LOOKUP, organism,
                                        false, label, code, id, null);
        pathQuery.addNode("Gene.homologues.homologue.organism")
                                .getConstraints().add(c2);

        Constraint c3 = new Constraint(ConstraintOp.EQUALS, "orthologue",
                                        false, label, code, id , null);
        pathQuery.addNode("Gene.homologues.type").getConstraints().add(c3);

        pathQuery.setConstraintLogic("A and B and C");
        pathQuery.syncLogicExpression("and");
        
        WebResultsExecutor executor = SessionMethods.getWebResultsExecutor(session);
        return executor.execute(pathQuery);
    }

    /**
     * {@inheritDoc}
     */
    public ActionMessage getActionMessage(Model model, String externalids, int convertedSize,
                                          String type, String organism)
                    throws UnsupportedEncodingException {
        PathQuery pathQuery = new PathQuery(model);

        List<Path> view = new ArrayList<Path>();
        view.add(PathQuery.makePath(model, pathQuery, "Gene.primaryIdentifier"));
        view.add(PathQuery.makePath(model, pathQuery, "Gene.organism.shortName"));
        view.add(PathQuery.makePath(model, pathQuery,
                        "Gene.homologues.homologue.primaryIdentifier"));
        view.add(PathQuery.makePath(model, pathQuery,
                                                "Gene.homologues.homologue.organism.shortName"));
        view.add(PathQuery.makePath(model, pathQuery, "Gene.homologues.type"));
        view.add(PathQuery.makePath(model, pathQuery, "Gene.homologues.inParanoidScore"));
        pathQuery.setViewPaths(view);

        String label = null, id = null, code = pathQuery.getUnusedConstraintCode();

        Constraint c = new Constraint(ConstraintOp.LOOKUP, externalids, false,
                                        label, code, id, null);
        pathQuery.addNode("Gene").getConstraints().add(c);

        pathQuery.addNode("Gene.homologues").setType("Homologue");

        Constraint c2 = new Constraint(ConstraintOp.EQUALS, "orthologue", false,
                                        label, code, id, null);
        pathQuery.addNode("Gene.homologues.type").getConstraints().add(c2);

        pathQuery.addNode("Gene.homologues.homologue").setType("Gene");

        pathQuery.addNode("Gene.homologues.homologue.organism").setType("Organism");

        Constraint c3 = new Constraint(ConstraintOp.LOOKUP, organism,
                        false, label, code, id, null);
        pathQuery.addNode("Gene.homologues.homologue.organism").getConstraints().add(c3);

        pathQuery.setConstraintLogic("A and B and C");
        pathQuery.syncLogicExpression("and");

        String query = pathQuery.toXml();
        String encodedurl = URLEncoder.encode(query, "UTF-8");
        String[] values = new String[]
            {
                String.valueOf(convertedSize),
                organism,
                String.valueOf(externalids.split(",").length),
                type,
                encodedurl
            };
        ActionMessage am = new ActionMessage("portal.orthologues", values);
        return am;
    }

}
