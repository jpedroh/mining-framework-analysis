using System.Net.Http;

namespace Org.OpenAPITools.Client
{
    /// <summary>
    /// Any Api client
    /// </summary>
    public interface IApi
    {
        /// <summary>
        /// The HttpClient
        /// </summary>
        HttpClient HttpClient { get; }

        /// <summary>
        /// An event to track the health of the server. 
        /// If you store these event args, be sure to purge old event args to prevent a memory leak.
        /// </summary>
        event ClientUtils.EventHandler<ApiResponseEventArgs> ApiResponded;
    }
}