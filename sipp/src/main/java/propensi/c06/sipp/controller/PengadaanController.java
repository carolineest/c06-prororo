package propensi.c06.sipp.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import propensi.c06.sipp.dto.PengadaanMapper;
import propensi.c06.sipp.dto.PengadaanRequestDTO;
import propensi.c06.sipp.dto.request.UpdatePengadaanRequestDTO;
import propensi.c06.sipp.model.BarangRencana;
import propensi.c06.sipp.model.Pengadaan;
import propensi.c06.sipp.model.PengadaanBarang;
import propensi.c06.sipp.model.Rencana;
import propensi.c06.sipp.repository.PengadaanDb;
import propensi.c06.sipp.service.BarangService;
import propensi.c06.sipp.service.PengadaanService;
import propensi.c06.sipp.service.RencanaService;
import propensi.c06.sipp.service.UserService;
import propensi.c06.sipp.service.VendorService;


@Controller
public class PengadaanController {

    @Autowired
    private PengadaanService pengadaanService;

    @Autowired
    private BarangService barangService;

    @Autowired
    private VendorService vendorService;

    @Autowired

    private PengadaanMapper pengadaanMapper;

    @Autowired
    private PengadaanDb pengadaanDb;

    @Autowired
    private RencanaService rencanaService;

    @Autowired
    private UserService userService;

    @GetMapping("/pengadaan")
    public String listPengadaan(Model model){
        //List<Pengadaan> listPengadaan = pengadaanService.getAllPengadaan();
        List<Pengadaan> listPengadaan = pengadaanService.getAllPengadaan();
        model.addAttribute("listPengadaan", listPengadaan);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);
        if(userService.getCurrentUserRole().equalsIgnoreCase("manajer") || userService.getCurrentUserRole().equalsIgnoreCase("keuangan")){
            return "viewAllPengadaanKeuanganManajer.html";
        } else{
            return "viewAllPengadaan.html";
        }
    }

    @GetMapping("/pengadaan/{id}")
    public String detailPengadaan(@PathVariable String id, Model model){
        Pengadaan pengadaan = pengadaanService.getPengadaanDetail(id);
        Map<String, Float> totalHargaMap = pengadaanService.hitungTotalHarga(pengadaan);

        float totalHargaAwal = totalHargaMap.get("totalHargaAwal");
        float totalHargaDiskonSatuan = totalHargaMap.get("totalHargaDiskonSatuan");
        float totalHargaAkhir = totalHargaMap.get("totalHargaAkhir");
        model.addAttribute("pengadaan", pengadaan);
        model.addAttribute("totalHargaAwal", totalHargaAwal);
        model.addAttribute("totalHargaDiskonSatuan", totalHargaDiskonSatuan);
        model.addAttribute("totalHargaAkhir", totalHargaAkhir);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        if(userService.getCurrentUserRole().equalsIgnoreCase("manajer") || userService.getCurrentUserRole().equalsIgnoreCase("keuangan")){
            return "detailPengadaanKeuanganManajer.html";
        } else{
            return "detailPengadaan.html";
        }

    }

    @GetMapping("/pengadaan/{id}/updateShipmentStatus")
    public String formUpdateShipmentStatusPengadaan(@PathVariable String id, Model model){
        Pengadaan pengadaan = pengadaanService.getPengadaanDetail(id);
        model.addAttribute("pengadaan", pengadaan);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        return "formUpdateShipmentStatusPengadaan";
    }

    @PostMapping("/pengadaan/{id}/updateShipmentStatus")
    public String updateStatusShipmentPengadaan(@PathVariable String id, @RequestParam("shipmentStatus") int shipmentStatus){
        Pengadaan pengadaan = pengadaanService.getPengadaanDetail(id);
        pengadaan.setShipmentStatus(shipmentStatus);
        //pengadaan.setPaymentStatus(paymentStatus);
        pengadaanService.updateStatusPengadaan(pengadaan); // Buat method ini di PengadaanService
        return "redirect:/pengadaan";
    }

    @GetMapping("/pengadaan/{id}/updatePaymentStatus")
    public String formUpdateStatusPengadaan(@PathVariable String id, Model model){
        Pengadaan pengadaan = pengadaanService.getPengadaanDetail(id);
        model.addAttribute("pengadaan", pengadaan);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        return "formUpdatePaymentStatusPengadaan";
    }

    @PostMapping("/pengadaan/{id}/updatePaymentStatus")
    public String updateStatusPengadaan(@PathVariable String id, @RequestParam("paymentStatus") int paymentStatus){
        Pengadaan pengadaan = pengadaanService.getPengadaanDetail(id);
        //pengadaan.setShipmentStatus(shipmentStatus);
        pengadaan.setPaymentStatus(paymentStatus);
        pengadaanService.updateStatusPengadaan(pengadaan); // Buat method ini di PengadaanService
        return "redirect:/pengadaan";
    }

    ///semifinall
    @GetMapping(value = "/pengadaan/{id}/update")
    public String formUpdate(@PathVariable(value = "id") String id, Model model) {
        var pengadaan = pengadaanService.getPengadaanDetail(id);
        // Periksa apakah pengadaan memenuhi syarat untuk diperbarui
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);
        if (pengadaan.getShipmentStatus() == 0 && pengadaan.getPaymentStatus() == 0) {
            var pengadaanDTO = pengadaanMapper.pengadaanToUpdatePengadaanRequestDTO(pengadaan);
            pengadaanDTO.setIdPengadaan(id);
            model.addAttribute("pengadaanDTO", pengadaanDTO);
            model.addAttribute("listVendor", vendorService.getAllVendors());
            model.addAttribute("listbarang", barangService.getAllBarang());
            return "updateForm";
        } else {
            // Tampilkan pesan bahwa pengadaan tidak dapat diperbarui
            model.addAttribute("errorMessage", "Pengadaan tidak memenuhi syarat untuk diperbarui.");
            return "error-view"; // Ganti dengan halaman atau tindakan yang sesuai
        }
    }

    ///hampirrrrrr//
    @PostMapping("pengadaan/{id}/update")
    public String updatePengadaan(@Valid @ModelAttribute UpdatePengadaanRequestDTO dto, BindingResult bindingResult,
                                  Model model, HttpServletRequest request) {

        if(request.getParameter("tambahRow") != null){
            if (dto.getListBarang() == null || dto.getListBarang().size() == 0) {
                dto.setListBarang(new ArrayList<>());
            }
            dto.getListBarang().add(new PengadaanBarang());
            model.addAttribute("pengadaanDTO", dto);
            model.addAttribute("listBarang", barangService.getAllBarang());
            model.addAttribute("listVendor", vendorService.getAllVendors());
            // ... add other attributes if needed ...
            return "updateForm";
        } else if(request.getParameter("hapusRow") != null){
            int rowIndex = Integer.parseInt(request.getParameter("hapusRow"));
            // ... remove row logic ...
            dto.getListBarang().remove(rowIndex);
            model.addAttribute("listBarang", barangService.getAllBarang());
            model.addAttribute("listVendor", vendorService.getAllVendors());
            model.addAttribute("pengadaanDTO", dto);
            // ... add other attributes if needed ...
            return "updateForm";

        } else{
            //var pengadaanFromDto = pengadaanMapper.updatePengadaanRequestDTOToPengadaan(dto);
            var pengadaan = pengadaanService.updatePengadaan(dto);
            model.addAttribute("idPengadaan", pengadaan.getIdPengadaan());
            System.out.println("ini idnyaa"+pengadaan.getIdPengadaan());
            String username = userService.getCurrentUserName();
            model.addAttribute("username", username);

            return "success-update-pengadaan";
        }
    }
/// di atas semi finaallll




    @GetMapping("/pengadaan/tambah")
    public String formAddPengadaan(@RequestParam(required=false) Long idRencana, Model model) {
        PengadaanRequestDTO dtoPengadaan = new PengadaanRequestDTO();

        if (idRencana != null) {
            Rencana rencana = rencanaService.getRencanaById(idRencana);
            dtoPengadaan.setNamaPengadaan(rencana.getNamaRencana());
            dtoPengadaan.setVendor(rencana.getVendor());
            dtoPengadaan.setTanggalPengadaan(rencana.getExpectedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dtoPengadaan.setListBarang(new ArrayList<>());
            for (BarangRencana barangRencana : rencana.getListBarangRencana()) {
                PengadaanBarang barangPengadaan = new PengadaanBarang();
                barangPengadaan.setBarang(barangRencana.getBarang());
                barangPengadaan.setJumlahBarang(barangRencana.getKuantitas());
                dtoPengadaan.getListBarang().add(barangPengadaan);
            }
            model.addAttribute("idRencana", idRencana);
        }

        model.addAttribute("dto", dtoPengadaan);
        model.addAttribute("listVendor", vendorService.getAllVendors());
        model.addAttribute("listBarang", barangService.getAllBarang());

        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);
        return "formAddPengadaan";
    }



    @PostMapping(value = "/pengadaan/tambah", params = {"addRow"})
    public String addRowTambahBarang(@ModelAttribute PengadaanRequestDTO dto, Model model, @RequestParam(required = false) Long idRencana) {

        if (dto.getListBarang() == null || dto.getListBarang().size() == 0) {
            dto.setListBarang(new ArrayList<>());
        }
        dto.getListBarang().add(new PengadaanBarang());
        model.addAttribute("dto", dto);
        model.addAttribute("idRencana", idRencana);
        model.addAttribute("listVendor", vendorService.getAllVendors());
        model.addAttribute("listBarang", barangService.getAllBarang());
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        return "formAddPengadaan";
    }


    @PostMapping(value = "/pengadaan/tambah", params = {"deleteRow"})
    public String deleteRowTambahBarang(Model model, @ModelAttribute PengadaanRequestDTO dto, @RequestParam("deleteRow") int row, @RequestParam(required = false) Long idRencana){

        dto.getListBarang().remove(row);
        model.addAttribute("dto", dto);
        model.addAttribute("idRencana", idRencana);
        model.addAttribute("listBarang", barangService.getAllBarang());
        model.addAttribute("listVendor", vendorService.getAllVendors());
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        return "formAddPengadaan";

    }

    @PostMapping("/pengadaan/tambah")
    public String addPengadaan(@Valid @ModelAttribute PengadaanRequestDTO dto, Model model, @RequestParam(required = false) Long idRencana){
//        Map<String, Integer> totalHargaMap = pengadaanService.hitungTotalHarga(dto);
//
//        int totalHargaAwal = totalHargaMap.get("totalHargaAwal");
//        int totalHargaSetelahDiskon = totalHargaMap.get("totalHargaSetelahDiskon");

        dto.setPaymentStatus(0);
        dto.setShipmentStatus(0);
        String id = dto.getIdPengadaan();

        if (idRencana != null) {
            Rencana rencana = rencanaService.getRencanaById(idRencana);
            rencanaService.ubahStatusRencana(rencana, "direalisasikan", rencana.getLogRencana().get(rencana.getLogRencana().size()-1).getFeedback());
            Pengadaan pengadaan = pengadaanService.addPengadaan(dto, rencana);
        } else {
            Pengadaan pengadaan = pengadaanService.addPengadaan(dto, null);
        }
        
        model.addAttribute("idPengadaan", id);
//        model.addAttribute("totalHargaAwal", totalHargaAwal);
//        model.addAttribute("totalHargaSetelahDiskon", totalHargaSetelahDiskon);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);

        return "successAddPengadaan";
    }


    @GetMapping("/pengadaan/{id}/delete")
    public String deletePengadaanBarang(@PathVariable("id") String id, Model model){
        pengadaanService.deletePengadaan(id);
        model.addAttribute("id", id);
        String username = userService.getCurrentUserName();
        model.addAttribute("username", username);
        return "successDeletePengadaan";
    }

}
