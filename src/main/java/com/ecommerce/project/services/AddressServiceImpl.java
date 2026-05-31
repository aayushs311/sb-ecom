package com.ecommerce.project.services;

import com.ecommerce.project.exceptions.MyResourceNotFoundException;
import com.ecommerce.project.models.Address;
import com.ecommerce.project.models.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(ModelMapper modelMapper, AddressRepository addressRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address addresses = addressRepository.findById(addressId)
                .orElseThrow(() -> new MyResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(addresses, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO) {
        Address addressToBeUpdated = addressRepository.findById(addressId)
                .orElseThrow(() -> new MyResourceNotFoundException("Address", "addressId", addressId));

        addressToBeUpdated.setCity(addressDTO.getCity());
        addressToBeUpdated.setPincode(addressDTO.getPincode());
        addressToBeUpdated.setBuildingName(addressDTO.getBuildingName());
        addressToBeUpdated.setStreet(addressDTO.getStreet());
        addressToBeUpdated.setCity(addressDTO.getCity());
        addressToBeUpdated.setCountry(addressDTO.getCountry());
        Address updatedAddress = addressRepository.save(addressToBeUpdated);

        User user = addressToBeUpdated.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(addressToBeUpdated);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressToBeDeleted = addressRepository.findById(addressId)
                .orElseThrow(() -> new MyResourceNotFoundException("Address", "addressId", addressId));
        User user = addressToBeDeleted.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(addressToBeDeleted);
        return "Address deleted successfully !!!";
    }
}
