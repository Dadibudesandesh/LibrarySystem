package com.librarySystem.controller;

import com.librarySystem.entity.Book;
import com.librarySystem.entity.BookRequest;
import com.librarySystem.entity.Borrow;
import com.librarySystem.entity.User;
import com.librarySystem.repository.BookRepository;
import com.librarySystem.repository.BookRequestRepository;
import com.librarySystem.repository.BorrowRepository;
import com.librarySystem.repository.UserRepository;
import com.librarySystem.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRequestService bookRequestService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private EmailService emailService;


    @GetMapping("/admin/addStudent")
    public String addStudentPage(HttpSession session) {
        Object loggedInUser = session.getAttribute("role");
        if (!(loggedInUser == "admin")) {
            return "redirect:/login";
        }
        return "admin/addStudent";
    }

    @GetMapping("/admin/manageUsers")
    public ModelAndView getStudent(HttpSession session){
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return new ModelAndView("redirect:/login");
        }
        List<User> list = userService.getAllUsers();
        return new ModelAndView("admin/manageUsers","users",list);
    }

    @GetMapping("/admin/registerRequest")
    public ModelAndView getUser(HttpSession session){
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return new ModelAndView("redirect:/login");
        }
        List<User> list = userService.findByStatus("PENDING");
        return new ModelAndView("admin/registerRequest","pendingUsers",list);
    }

    @GetMapping("/admin/addBook")
    public String addBooksPage(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return "redirect:/login";
        }
        return "admin/addBook";
    }

    @GetMapping("/admin/manageBook")
    public ModelAndView getBookPage(HttpSession session){
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return new ModelAndView("redirect:/login");
        }
        List<Book> list = bookService.getAllBook();
        return new ModelAndView("admin/manageBook","books",list);
    }


//    @GetMapping("/admin/statistic")
//    public String showStatistics(Model model) {
//        Map<String, Integer> returnedBooksByMonth = getReturnedBooksByMonth(); // Ensure this method returns a valid map
//
//        if (returnedBooksByMonth == null) {
//            returnedBooksByMonth = new HashMap<>(); // Prevent null error
//        }
//
//        model.addAttribute("returnedBooksByMonth", returnedBooksByMonth);
//        return "admin/statistic";
//    }


    @GetMapping("/admin/borrowBook")
    public String borrowBookPage(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return "redirect:/login";
        }
        return "admin/borrowBook";
    }


    @PostMapping("/saveBook")
    public String addBook(@ModelAttribute Book b) {
        bookService.save(b);
        return "redirect:/admin/addBook";
    }




    @RequestMapping("/updateUser/{id}")
    public String updateUser(@PathVariable Long id,
                             Model model,
                             HttpSession session) {

        Object role = session.getAttribute("role");

        if (role == null || !role.equals("admin")) {
            return "redirect:/login";
        }

        User user = userService.getUserById(id);

        List<String> roles = List.of(
                "admin",
                "student"
        );

        List<String> statuses = List.of(
                "PENDING",
                "APPROVED",
                "REJECTED"
        );

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("statuses", statuses);

        return "admin/updateUser";
    }

    @PostMapping("/saveUpdatedUser")
    public String saveUpdatedUser(@ModelAttribute User user,RedirectAttributes redirectAttributes) {
        userService.save(user);
        return "redirect:/admin/manageUsers";
    }

    @RequestMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteById(id);
        return "redirect:/admin/manageUsers";
    }

    @GetMapping("/admin/statistics")
    public String showStatisticsPage(Model model) {
        model.addAttribute("returnedBooksByMonth", statisticsService.getReturnedBooksByMonth());
        model.addAttribute("pendingBooks", statisticsService.getPendingBooks());
        model.addAttribute("totalBorrowed", statisticsService.getTotalBorrowedBooks());
        model.addAttribute("totalReturned", statisticsService.getTotalReturnedBooks());
        model.addAttribute("mostBorrowedBooks", statisticsService.getMostBorrowedBooks());
        model.addAttribute("activeBorrowers", statisticsService.getActiveBorrowers());
        return "admin/statistic";
    }


    @GetMapping("/admin/bookRequests")
    public String viewRequests(Model model , HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("requests", bookRequestRepository.findByStatus("PENDING"));
        return "admin/bookRequests";
    }


    @GetMapping("/request-borrow/{bookId}")
    public String requestBook(@PathVariable int bookId, HttpSession session, RedirectAttributes redirectAttributes) {
        User student = (User) session.getAttribute("loggedInUser");

        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "You need to log in first.");
            return "redirect:/login";
        }

        Book book = bookService.getBookById(bookId);

        if (book != null) {
            BookRequest bookRequest = new BookRequest();
            bookRequest.setStudent(student);
            bookRequest.setBook(book);
            bookRequest.setStatus("PENDING");

            bookRequestService.saveBookRequest(bookRequest);

            redirectAttributes.addFlashAttribute("message", "Book request sent successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book not found.");
        }

        return "redirect:/student/borrowRequest";
    }



    @PostMapping("/approve-request/{id}")
    public String approveRequest(@PathVariable Long id) {

        BookRequest request = bookRequestRepository.findById(id).orElse(null);

        if(request != null){

            User student = request.getStudent();
            Book book = request.getBook();

            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(7);

            Borrow borrow = new Borrow();

            borrow.setStudent(student);
            borrow.setBook(book);
            borrow.setBorrowDate(borrowDate);
            borrow.setDueDate(dueDate);
            borrow.setStatus("borrowed");

            borrowRepository.save(borrow);

            request.setStatus("Approved");
            bookRequestRepository.save(request);

            emailService.sendBookApprovalEmail(
                    student.getEmail(),
                    student.getName(),
                    book.getName(),
                    dueDate
            );
        }

        return "redirect:/admin/bookRequests";
    }

    @PostMapping("/reject-request/{id}")
    public String rejectRequest(@PathVariable Long id) {
        BookRequest request = bookRequestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus("REJECTED");
            bookRequestRepository.save(request);
        }
        return "redirect:/admin/bookRequests";
    }

    @GetMapping("/admin/manageBorrow")
    public String manageBorrowPage(HttpSession session,Model model){
        Object role = session.getAttribute("role");
        if (role == null || !role.equals("admin")) {
            return "redirect:/login";
        }
        List<Borrow> list = borrowService.getAllBorrow();

        model.addAttribute("borrowList",list);
        return "admin/manageBorrow";
    }

    @GetMapping("/rejectUser/{id}")
    public String rejectUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setStatus("REJECT");
            userRepository.save(user);
        }
        return "redirect:/admin/registerRequest";
    }

    @GetMapping("/approveUser/{id}")
    public String approveUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setStatus("APPROVED");
            userRepository.save(user);
        }
        return "redirect:/admin/registerRequest";
    }


}
