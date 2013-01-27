using QRCodeLocalizer.Resources;

namespace QRCodeLocalizer
{
    /// <summary>
    /// Provides access to string resources.
    /// </summary>
    public class LocalizedStrings
    {
        private static readonly AppResources sLocalizedResources = new AppResources();

        public AppResources LocalizedResources { get { return sLocalizedResources; } }
    }
}