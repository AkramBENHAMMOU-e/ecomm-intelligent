import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.proj.ecommintelligent.entities.Order;
import com.proj.ecommintelligent.entities.Customer;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    public void sendOrderConfirmationEmail(Order order) {
        // Vérifier si l'envoi d'email est activé
        if (!mailEnabled) {
            System.out.println("Envoi d'email désactivé pour la commande #" + order.getId());
            return;
        }

        Customer customer = order.getCustomer();
        
        // Vérifier que le client a un email
        if (customer == null || customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            System.out.println("Aucun email trouvé pour le client de la commande #" + order.getId());
            return;
        }

        String customerEmail = customer.getEmail().trim();
        String customerName = customer.getFirstName() + " " + customer.getLastName();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(customerEmail);
        message.setSubject("Confirmation de votre commande #" + order.getId());
        
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Bonjour ").append(customerName).append(",\n\n");
        textBuilder.append("Merci pour votre commande !\n\n");
        textBuilder.append("Voici un résumé de votre commande #").append(order.getId()).append(":\n\n");
        
        // Détails des articles
        textBuilder.append("Articles commandés:\n");
        double totalAmount = 0;
        for (var item : order.getItems()) {
            double itemTotal = item.getPrice() * item.getQuantity();
            totalAmount += itemTotal;
            textBuilder.append("- ").append(item.getProduct().getName())
                      .append(" (Quantité: ").append(item.getQuantity())
                      .append(", Prix unitaire: ").append(item.getPrice()).append(" DH")
                      .append(", Sous-total: ").append(itemTotal).append(" DH)\n");
        }
        
        textBuilder.append("\nTotal de la commande: ").append(totalAmount).append(" DH\n\n");
        textBuilder.append("Statut: ").append(order.getStatus()).append("\n");
        textBuilder.append("Date de commande: ").append(order.getDate()).append("\n\n");
        textBuilder.append("Nous traiterons votre commande dans les plus brefs délais.\n\n");
        textBuilder.append("Cordialement,\nL'équipe de votre boutique e-commerce.");

        message.setText(textBuilder.toString());
        
        try {
            mailSender.send(message);
            System.out.println("Email de confirmation envoyé à " + customerEmail + " pour la commande #" + order.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + customerEmail + ": " + e.getMessage());
        }
    }
}