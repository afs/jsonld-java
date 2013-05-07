package com.github.jsonldjava.impl;


import java.util.HashMap;
import java.util.Map;

import org.apache.clerezza.rdf.core.*;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.clerezza.rdf.core.impl.TypedLiteralImpl;

import com.github.jsonldjava.core.JSONLDTripleCallback;

public class ClerezzaTripleCallback implements JSONLDTripleCallback {

    private MGraph mGraph = new SimpleMGraph();
    private Map<String, BNode> bNodeMap = new HashMap<String, BNode>();

    public void setMGraph(MGraph mGraph) {
        this.mGraph = mGraph;
        bNodeMap = new HashMap<String, BNode>();
    }

    public MGraph getMGraph() {
        return mGraph;
    }

    @Override
    public void triple(String s, String p, String o, String graph) {
        if (s == null || p == null || o == null) {
            // TODO: i don't know what to do here!!!!
            return;
        }

        NonLiteral subject = getNonLiteral(s);
		UriRef predicate = new UriRef(p);
		NonLiteral object = getNonLiteral(o);
		mGraph.add(new TripleImpl(subject, predicate, object));
    }

    @Override
    public void triple(String s, String p, String value, String datatype, String language, String graph) {
        NonLiteral subject = getNonLiteral(s);
		UriRef predicate = new UriRef(p);
		Resource object;
		if (language != null) {
			object = new PlainLiteralImpl(value, new Language(language)); 
		} else {
			if (datatype != null) {
				object = new TypedLiteralImpl(value, new UriRef(datatype));
			} else {
				object = new PlainLiteralImpl(value);
			}
		}
      
		mGraph.add(new TripleImpl(subject, predicate, object));
    }

	private NonLiteral getNonLiteral(String s) {
		if (s.startsWith("_:")) {
			return getBNode(s);
		} else {
			return new UriRef(s);
		}
	}

	private BNode getBNode(String s) {
		if (bNodeMap.containsKey(s)) {
			return bNodeMap.get(s);
		} else {
			BNode result = new BNode();
			bNodeMap.put(s, result);
			return result;
		}
	}

	@Override
	public void triple(String s, String p, String o) {
		triple(s, p, o, null);
	}

	@Override
	public void triple(String s, String p, String value, String datatype,
			String language) {
		triple(s, p, value, datatype, language, null);
	}

	@Override
	public void processIgnored(Object parent, String parentId, String key,
			Object value) {
		// nothing to process
	}

}
