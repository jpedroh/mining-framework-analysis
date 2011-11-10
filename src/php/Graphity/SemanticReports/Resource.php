<?php

namespace Graphity\SemanticReports;

use Graphity\Rdf as Rdf;
use Graphity\Request;
use Graphity\Resource;
use Graphity\Response;
use Graphity\Router;
use Graphity\Sparql as Sparql;
use Graphity\Util\UriBuilder;
use Graphity\Util\PhutilURI;
use Graphity\WebApplicationException;

class Resource extends Resource
{
    /**
     *  @var Repository
     */
    private $repository = null;

    /**
     *  @var DataCache
     */
    private $dataCache = null;

    /**
     * Array with post urls for random retrieval.
     * 
     * @var array
     */
    private $postUris = array();

    /**
     *  @var Form
     */
    private $form = null;

    /**
     *  @var View
     */
    private $view = null;

    public function __construct(Request $request, Router $router) {
        parent::__construct($request, $router); 
        $this->form = new Form($request);

        $environment = "prod";
        if(strpos($this->getBaseURI(), "local.") !== false) {
            $environment = "local";
        }
        if(strpos($this->getBaseURI(), "stage.") !== false) {
            $environment = "stage";
        }
        Config::reload($environment);

        //$this->repository = new DydraRepository(new DydraClient(Config::get('dydra.accountId'), Config::get('dydra.apiKey')), Config::get('dydra.repositoryId'));
        $this->repository = new FusekiRepository(new FusekiClient(Config::get('fuseki.host')), Config::get('fuseki.repositoryId'));

        try {
            $this->dataCache = new Cache\DataCache("localhost", 11211, $environment . "_");
        } catch(\Exception $e) {
            $this->log($e);
            $this->dataCache = new Cache\FakeCache;
        }
    }

    /** 
     * @GET
     * @Produces("text/html")
     * @Produces("application/xml")
     * @Produces("application/xhtml+xml")
     * @Produces("application/rdf+xml")
     */
    public function doGet()
    {
        /**
         *  strpos(....) are commented out, because it appears that sometimes you can receive an application/rdf+xml RSS content instead of HTML
         *  if the browser accidentally or intentionally sends application/rdf+xml among other accept mime-types.
         */
        if ($this->getRequest()->getParameter("view") == "rss")/* || strpos($this->getRequest()->getHeader('HTTP_ACCEPT'), "application/rdf+xml") !== false)*/
            return new RSSView($this);
        if ($this->getRequest()->getParameter("view") == "rdf")/* || strpos($this->getRequest()->getHeader('HTTP_ACCEPT'), "application/rdf+xml") !== false)*/
            return new RDFXMLView($this);
        if ($this->getRequest()->getParameter("view") == "json")
            return new JSONView($this);
        if ($this->getRequest()->getParameter("offset") !== null) {
            return new BobsLynMoreView($this);
        }

        return $this->getDefaultView();
    }

    /** 
     * @POST
     * @Consumes("application/x-www-form-urlencoded")
     * @Produces("text/html")
     * @Produces("application/xml")
     * @Produces("application/xhtml+xml")
     * @Produces("application/json")
     */
    public function doPost()
    {
        if($this->getRequest()->getParameter("rating") !== null) {
            $this->rate($this->getRequest()->getParameter("rating"));
            
            return new RatingJSONView($this);
        }

        return $this->getDefaultView();
    }
 
    /**
     * @return Form
     */
    public function getForm()
    {
        return $this->form;
    }

    /**
     * @return DydraRepository
     */
    public function getRepository()
    {
        return $this->repository;
    }

    /**
     * @return DataCache
     */
    public function getDataCache()
    {
        return $this->dataCache;
    }
    
    /**
     * @param string $relativePath
     * @return string
     */
    public function getQueryString($relativePath)
    {
        return file_get_contents(ROOTDIR . DS . "src" . DS . "main" . HeltNormaltView::SPARQL_BASE . $relativePath);
    }

    public function getDefaultView()
    {
        if ($this->view == null) $this->view = new HeltNormaltView($this); // cannot be initialized in constructor as long it uses $this
        return $this->view;
    }

    /**
     * @return boolean
     */
    public function exists()
    {
        $today = new \DateTime();
        return $this->getRepository()->ask(Sparql\Query::newInstance()
            ->setQuery($this->getQueryString('exists.rq'))
            ->setVariable('uri', new Rdf\Resource($this->getURI()))
            ->setVariable('today', new Rdf\Literal($today->format(\DateTime::W3C), Model\XSD::dateTime)));
        /**
        return $this->getRepository()->ask($this->getQueryString('exists.rq'), array(
            'resource-uri' => $this->getURI(), 
            'today' => $today->format(\DateTime::W3C)
        ));
        */
    }

    public function getOntClass()
    {
        $className = get_class($this);
        $namespace = '\\';
        if(false !== ($nsPos = strripos($className, "\\"))) {
            $namespace = substr($className, 0, $nsPos);
            $className = substr($className, $nsPos+1);
        }
        $className = str_replace("List", "", $className);

        return Model\HeltNormalt::NS . $className;
    }

    /**
     * @return string
     */
    public function describe() // can this be refactored into another method that returns Response??
    {
        $today = new \DateTime();

        $className = get_class($this);
        $namespace = '\\';
        if(false !== ($nsPos = strripos($className, "\\"))) {
            $namespace = substr($className, 0, $nsPos);
            $className = substr($className, $nsPos+1);
        }

        // TO-DO: move the following relationships to ontology!
        $fileName = "describe.rq";
        if($className == "FrontPageResource") $fileName = "latestPosts.rq";
        else
        {
            if (strpos($className, "ListResource") !== false)
            {
                $resourceName = str_replace("ListResource", "", $className); 
                if (isset($this->moduleMap[$resourceName])) $fileName = $this->moduleMap[$resourceName] . "/list.rq";
            }
            else
            {
                $resourceName = str_replace("Resource", "", $className); 
                if (isset($this->moduleMap[$resourceName])) $fileName = $this->moduleMap[$resourceName] . "/read.rq";
            }
        }

        return $this->getRepository()->query(Sparql\Query::newInstance()
            ->setQuery($this->getQueryString($fileName))
            ->setVariable("baseUri", new Rdf\Resource($this->getBaseURI()))
            ->setVariable("uri", new Rdf\Resource($this->getURI()))
            ->setVariable("today", new Rdf\Literal($today->format(\DateTime::W3C), Model\XSD::dateTime))
            ->setParameter("orderDir", new Sparql\Keyword($this->getForm()->getAscDescString()))
            ->setParameter("orderBy", new Sparql\Variable($this->getForm()->getOrderBy()))
            ->setParameter("offset", new Sparql\Integer(intval($this->getForm()->getOffset())))
            ->setParameter("limit", new Sparql\Integer(intval($this->getForm()->getLimit()))),
        DydraClient::RDFXML);
    }

}
