package home.com.myapplication;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

public class CustomXYTileSource extends OnlineTileSourceBase {
    public CustomXYTileSource(final String aName, final int aZoomMinLevel,
                        final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                        final String[] aBaseUrl) {
        this(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl,null);
    }

    public CustomXYTileSource(final String aName, final int aZoomMinLevel,
                        final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                        final String[] aBaseUrl, final String copyright) {
      //  super(aName,ResourceProxy.string.mapnik,aZoomMinLevel,aZoomMaxLevel,aTileSizePixels,aImageFilenameEnding,aBaseUrl);

        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl,copyright);
    }

    @Override
    public String toString(){
        return name();
    }

    /*@Override
    public String getTileURLString(MapTile mapTile) {
       // mapTile.
        return getBaseUrl() +"x=" +mapTile.getX() + "&y=" + mapTile.getY()+"&z=" + mapTile.getZoomLevel();

    }*/

    @Override
    public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() +"x=" +MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex)+"&z=" + MapTileIndex.getZoom(pMapTileIndex);
        //return getBaseUrl() + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex) + "/" + MapTileIndex.getY(pMapTileIndex)
             //   + mImageFilenameEnding;
    }
}
