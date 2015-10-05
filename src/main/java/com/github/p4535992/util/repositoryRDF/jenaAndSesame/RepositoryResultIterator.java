package com.github.p4535992.util.repositoryRDF.jenaAndSesame;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.NiceIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 02/10/2015.
 * @author 4535992.
 * @version 2015.10.02.
 */
public class RepositoryResultIterator extends NiceIterator<Triple>
{
    org.openrdf.repository.RepositoryResult<org.openrdf.model.Statement> iter ;

    public RepositoryResultIterator(
            org.openrdf.repository.RepositoryResult<org.openrdf.model.Statement> iter1) {
        iter = iter1 ;
    }

    @Override
    public void close()
    {
        try { iter.close() ; }
        catch (org.openrdf.repository.RepositoryException ex) { throw new JenaException(ex) ; }
    }

    @Override
    public boolean hasNext()
    {
        try {  return iter.hasNext() ; }
        catch (org.openrdf.repository.RepositoryException ex) { throw new JenaException(ex) ; }
    }

    @Override
    public Triple next()
    {
        try { org.openrdf.model.Statement stmt = iter.next(); return JenaAndSesame.statementToTriple(stmt) ; }
        catch (org.openrdf.repository.RepositoryException ex) { throw new JenaException(ex) ; }
    }

    @Override
    public void remove()
    {
        try { iter.remove() ; }
        catch (org.openrdf.repository.RepositoryException ex) { throw new JenaException(ex) ; }
    }
}