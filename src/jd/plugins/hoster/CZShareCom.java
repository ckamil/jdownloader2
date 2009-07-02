//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.hoster;

import java.io.IOException;
import java.util.Locale;

import jd.PluginWrapper;
import jd.http.Encoding;
import jd.http.URLConnectionAdapter;
import jd.parser.Regex;
import jd.parser.html.Form;
import jd.plugins.Account;
import jd.plugins.AccountInfo;
import jd.plugins.DownloadLink;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.HostPlugin;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.utils.locale.JDL;

@HostPlugin(names = { "czshare.com"}, urls ={ "http://[\\w\\.]*?czshare\\.com/(files/\\d+/[\\w_]+|\\d+/[\\w_]+/[^\\s]+|download_file\\.php\\?id=\\d+&file=[^\\s]+)"}, flags = {2})
public class CZShareCom extends PluginForHost {

    private int simultanpremium = 20;

    public CZShareCom(PluginWrapper wrapper) {
        super(wrapper);
        enablePremium("http://czshare.com/create_user.php");
    }

    // @Override
    public void handleFree(DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        if (!br.containsHTML("value=\"FREE download\"")) throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, JDL.L("plugins.hoster.CZShareCom.nofreeslots", "No free slots available"), 60 * 1000);
        Form down = br.getFormBySubmitvalue("FREE+download");
        if (down == null) br.getFormbyProperty("action", Encoding.urlEncode("http://czshare.com/trust_me.php"));
        if (down == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFEKT);
        br.submitForm(down);
        down = null;
        if (br.containsHTML("Chyba 6 / Error 6")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, 60 * 60 * 1000);
        br.setFollowRedirects(true);
        String freepage = br.getRegex("form action=\"(.*?)\"").getMatch(0);
        String freesubmit = br.getRegex("type=\"submit\" value=\"(.*?)\"").getMatch(0);
        String freeid = br.getRegex("name=\"id\" value=\"(.*?)\"").getMatch(0);
        String freefile = br.getRegex("name=\"file\" value=\"(.*?)\"").getMatch(0);
        String freeticket = br.getRegex("name=\"ticket\" value=\"(.*?)\"").getMatch(0);
        String dlurl = "http://czshare.com/" + freepage + "?id=" + freeid + "&file=" + freefile + "&ticket=" + freeticket + "&captchastring=CAPTCHACODEGOESHERE&submit=" + freesubmit;
        String captchaurl = br.getRegex("img src=\"(captcha\\.php\\?ticket=.*?)\"").getMatch(0);
        captchaurl = "http://czshare.com/" + captchaurl;
        String code = getCaptchaCode(captchaurl, downloadLink);
        dlurl = dlurl.replace("CAPTCHACODEGOESHERE", code);
        br.getPage(dlurl);
        if (br.containsHTML("Chyba 6 / Error 6")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED, 60 * 60 * 1000);
        if (br.containsHTML("Nesouhlasi kontrolni kod")) throw new PluginException(LinkStatus.ERROR_CAPTCHA);
        Form down2 = br.getFormbyProperty("name", "pre_download_form");
        if (down2 == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFEKT);
        dl = br.openDownload(downloadLink, down2, false, 1);
        dl.startDownload();
    }

    // @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 20;
    }

    private void login(Account account) throws Exception {
        // this.setBrowserExclusive();
        br.setFollowRedirects(true);
        // br.clearCookies("czshare.com");
        br.getPage("http://czshare.com/prihlasit.php");
        Form login = br.getForm(0);
        login.put("jmeno", Encoding.urlEncode(account.getUser()));
        login.put("heslo", Encoding.urlEncode(account.getPass()));
        login.put("trvale", "0");
        br.submitForm(login);
        if (!br.containsHTML("odhl.sit")) throw new PluginException(LinkStatus.ERROR_PREMIUM, LinkStatus.VALUE_ID_PREMIUM_DISABLE);
    }

    // @Override
    public AccountInfo fetchAccountInfo(Account account) throws Exception {
        AccountInfo ai = new AccountInfo(this, account);
        try {
            login(account);
        } catch (PluginException e) {
            ai.setValid(false);
            return ai;
        }
        br.getPage("http://czshare.com/profi/platnost.php");
        if (br.containsHTML("Nemáte žádný platný kredit")) {
            ai.setValid(false);
            ai.setStatus(JDL.L("plugins.hoster.CZShareCom.nocreditleft", "No traffic credit left"));
            return ai;
        }
        String trafficleft = br.getRegex("Platnost do</td>[^d]*d>(.*?)</td>").getMatch(0);
        String expires = br.getRegex("Platnost do</td>.*<td>.*<td>(.*?)</td>").getMatch(0);
        if (expires != null) ai.setValidUntil(Regex.getMilliSeconds(expires, "dd.MM.yy HH:mm", Locale.GERMANY));
        if (trafficleft != null) ai.setTrafficLeft(trafficleft);

        // Logout
        br.getPage("http://czshare.com/profi/index.php?odhlasit=ano");
        return ai;
    }

    // @Override
    public int getMaxSimultanPremiumDownloadNum() {
        return simultanpremium;
    }

    // @Override
    public void handlePremium(DownloadLink downloadLink, Account account) throws Exception {
        br.setFollowRedirects(true);
        String linkurl = null;
        requestFileInformation(downloadLink);
        Form profidown = br.getFormBySubmitvalue("PROFI+download");
        if (profidown == null) br.getFormbyProperty("action", Encoding.urlEncode("http://czshare.com/profi/profi_down.php"));
        if (profidown == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFEKT);
        String id = br.getRegex("type=\"hidden\" name=\"id\" value=\"(.*?)\"").getMatch(0);
        br.submitForm(profidown);
        Form login = br.getForm(0);
        login.put("jmeno", Encoding.urlEncode(account.getUser()));
        login.put("heslo", Encoding.urlEncode(account.getPass()));
        login.put("trvale", "0");
        br.submitForm(login);
        // find premium links
        String[] links = br.getRegex("<td class=\"table2-black\"><a href=\"(.*?)\"").getColumn(0);
        // choose link with proper ID
        for (int i = 0; i < links.length; i++) {
            if (links[i].contains("=" + id + "&")) {
                linkurl = links[i];
                break;
            }
            if (links[i].contains("/" + id + "/")) {
                linkurl = null;
                br.setFollowRedirects(false);
                br.getPage(links[i]);
                linkurl = br.getRedirectLocation();
                if (linkurl == null) linkurl = links[i];
                br.setFollowRedirects(true);
                break;
            }
        }

        if (linkurl == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFEKT);

        dl = br.openDownload(downloadLink, linkurl, true, 0);
        URLConnectionAdapter con = dl.getConnection();
        if (!con.isOK()) {
            con.disconnect();
            throw new PluginException(LinkStatus.ERROR_PREMIUM);
        }
        dl.startDownload();
        // Remove link if finished
        if (downloadLink.getLinkStatus().hasStatus(LinkStatus.FINISHED)) {
            String kod = new Regex(linkurl, "kod=([a-zA-Z0-9_]+)&").getMatch(0);
            if (kod != null) br.postPage("http://czshare.com/profi/smazat_profi.php", "smaz%5B%5D=" + kod);
        }
        // Logout
        br.getPage("http://czshare.com/profi/index.php?odhlasit=ano");

    }

    // @Override
    public String getAGBLink() {
        return "http://www.czshare.com/pravidla.html";
    }

    // @Override
    public AvailableStatus requestFileInformation(DownloadLink downloadLink) throws IOException, PluginException {
        this.setBrowserExclusive();
        br.getPage(downloadLink.getDownloadURL());
        if (br.containsHTML("Soubor nenalezen")) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        String filename = Encoding.htmlDecode(br.getRegex("souboru:</strong></span>\\s<span[^>]*><strong>(.*?)</strong>").getMatch(0));
        String filesize = br.getRegex("Velikost:</td>\\s+<td[^>]*>(.*?)</td>").getMatch(0);
        if (filename == null || filesize == null) throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        downloadLink.setName(filename.trim());
        downloadLink.setDownloadSize(Regex.getSize(filesize));
        return AvailableStatus.TRUE;
    }

    // @Override
    public String getVersion() {
        return getVersion("$Revision$");
    }

    // @Override
    public void reset() {
    }

    // @Override
    public void resetPluginGlobals() {
    }

    // @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}
